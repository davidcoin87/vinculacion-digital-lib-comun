package com.fondopresente.dominio.servicio;

import com.fondopresente.dominio.archivo_adjunto.ArchivoAdjunto;

import java.util.Map;

public interface ServicioEnviarMail {

	/**
	 * Metodo que permite enviar un correo
	 * 
	 * @param emailPara
	 * @param asunto
	 * @param cuerpoMensaje
	 */
	void enviarMensaje(String emailPara, String asunto, String cuerpoMensaje);

	/**
	 * Metodo que permite enviar un correo
	 *
	 * @param emailPara
	 * @param asunto
	 * @param cuerpoMensaje
	 */
	void enviarMensajeHTML(String emailPara, String asunto, Map<String, String> cuerpoMensaje);

	/**
	 * Metodo que permite enviar un correo con adjunto
	 *
	 * @param emailPara
	 * @param asunto
	 * @param cuerpoMensaje
	 * @param archivoAdjunto
	 */
	void enviarMensajeHTMLConAdjunto(String emailPara, String asunto, Map<String, String> cuerpoMensaje, ArchivoAdjunto archivoAdjunto);

}
