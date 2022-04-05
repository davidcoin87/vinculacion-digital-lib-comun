package com.fondopresente.infraestructura.servicio;

import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import com.fondopresente.dominio.archivo_adjunto.ArchivoAdjunto;
import com.fondopresente.infraestructura.aspecto.LogTiempoDeEjecucion;
import com.fondopresente.infraestructura.leer_template.Template;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.fondopresente.dominio.excepcion.ExcepcionDeEmail;
import com.fondopresente.dominio.servicio.ServicioEnviarMail;
import com.sun.mail.util.MailConnectException;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class ServicioEnviarMailBase implements ServicioEnviarMail {

	private static final Logger LOGGER = LoggerFactory.getLogger(ServicioEnviarMailBase.class);
	private static final String KEY_PATH_EMAIL_TEMPLATE = "KEY_PATH_EMAIL_TEMPLATE";
	private static final String ERROR_ENVIANDO_EMAIL = "ERROR ENVIANDO E-MAIL";
	private static final String EMAIL_ENVIADO_A = "E-MAIL ENVIADO A: ";
	private static final String AUTENTICACION_FALLIDA = "Autenticación fallida";
	private static final String PARAMETROS_DEL_CORREO_SON_NULOS = "Parametros del correo son nulos";
	private static final String CONEXION_AL_SERVIDOR_FALLIDA = "Conexión al servidor fallida";

	@Autowired
	private JavaMailSender javaMailSender;

	@Autowired
	private Environment environment;

	private MimeMessageHelper mimeMessageHelper;
	private MimeMessage mimeMessage;

	@Override
	public void enviarMensaje(String emailPara, String asunto, String cuerpoMensaje) {
		crearEnvioEmailBasico(emailPara, asunto, cuerpoMensaje);
		enviarEmail(emailPara);
	}

	@Override
	@LogTiempoDeEjecucion
	public void enviarMensajeHTML(String emailPara, String asunto, Map<String, String> cuerpoMensaje) {
		String bodyMensaje = Template.obtener(this.environment.getProperty(cuerpoMensaje.get(KEY_PATH_EMAIL_TEMPLATE)));
		crearEnvioEmailBasico(emailPara, asunto, remplazaeDatosDinamicosCuerpoMensaje(bodyMensaje, cuerpoMensaje));
		enviarEmail(emailPara);
	}

	@Override
	public void enviarMensajeHTMLConAdjunto(String emailPara, String asunto, Map<String, String> cuerpoMensaje, ArchivoAdjunto archivoAdjunto) {
		String bodyMensaje = Template.obtener(this.environment.getProperty(cuerpoMensaje.get(KEY_PATH_EMAIL_TEMPLATE)));
		crearEnvioEmailBasico(emailPara, asunto, remplazaeDatosDinamicosCuerpoMensaje(bodyMensaje, cuerpoMensaje));
		adjuntarArchicoAlEmail(archivoAdjunto);
		enviarEmail(emailPara);
	}

	private String remplazaeDatosDinamicosCuerpoMensaje(String cuerpoMensaje, Map<String, String> datosDinamicosCuerpoMensaje) {
		AtomicReference<String> cuerpoMensajeNuevo = new AtomicReference<>();
		cuerpoMensajeNuevo.set(cuerpoMensaje);
		datosDinamicosCuerpoMensaje.forEach((key, value) -> cuerpoMensajeNuevo.set(cuerpoMensajeNuevo.get().replaceAll(key, value)));
		return cuerpoMensajeNuevo.get();
	}

	@CircuitBreaker(name = "rating-reintento-envio-email")
	@Retry(name = "reintento-envio-email")
	private void crearEnvioEmailBasico(String emailPara, String asunto, String cuerpoMensaje) {
		try {
			this.mimeMessage = this.javaMailSender.createMimeMessage();
			this.mimeMessageHelper = new MimeMessageHelper(mimeMessage, true,  "ISO-8859-1");
			this.mimeMessageHelper.setTo(emailPara);
			this.mimeMessageHelper.setSubject(asunto);
			this.mimeMessageHelper.setText(cuerpoMensaje, true);

		}	catch (MailConnectException e) {
			imprimirLogYLanzarExceptcion(e, CONEXION_AL_SERVIDOR_FALLIDA);
		}	catch (MessagingException e) {
			imprimirLogYLanzarExceptcion(e, ERROR_ENVIANDO_EMAIL);
		}
	}

	@CircuitBreaker(name = "rating-reintento-envio-email")
	@Retry(name = "reintento-envio-email")
	private void enviarEmail(String emailPara) {
		try {
			javaMailSender.send(this.mimeMessage);

			LOGGER.info(EMAIL_ENVIADO_A.concat(emailPara));
		} catch (IllegalArgumentException e) {
			imprimirLogYLanzarExceptcion(e, PARAMETROS_DEL_CORREO_SON_NULOS);
		} catch (MailAuthenticationException e) {
			imprimirLogYLanzarExceptcion(e, AUTENTICACION_FALLIDA);
		}
	}

	private void adjuntarArchicoAlEmail(ArchivoAdjunto archivoAdjunto) {
		File file = new File(archivoAdjunto.getNombreArchivoAdjunto());

		try {
			FileUtils.writeByteArrayToFile(file, archivoAdjunto.getArchivoAdjunto());
			DataSource source = new FileDataSource(file);
			mimeMessageHelper.addAttachment(archivoAdjunto.getNombreArchivoAdjunto(), source);
			file.deleteOnExit();
		} catch (MessagingException | IOException e) {
			imprimirLogYLanzarExceptcion(e, ERROR_ENVIANDO_EMAIL);
		}

	}

	private void imprimirLogYLanzarExceptcion(Exception e, String messageExcepcion){
		LOGGER.error(ERROR_ENVIANDO_EMAIL.concat(": ").concat(e.getMessage()));
		LOGGER.error(ERROR_ENVIANDO_EMAIL, e);
		throw new ExcepcionDeEmail(messageExcepcion);
	}

}
