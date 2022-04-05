package com.fondopresente.infraestructura.servicio;

import com.fondopresente.infraestructura.excepcion.ExcepcionTecnica;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ServicioExportarDocumentoPdfJasper {

	private static final Logger LOGGER = LoggerFactory.getLogger(ServicioExportarDocumentoPdfJasper.class);

	private static final String ERROR_CREANDO_EL_DOCUMENTO = "ERROR_CREANDO_EL_DOCUMENTO";
	private static final String ERROR_GENERANDO_EL_DOCUMENTO = "ERROR_GENERANDO_EL_DOCUMENTO";
	private static final String EXTENSION_PDF = ".pdf";

	public <T> byte[] ejecutar(String jasperDocumento, Map<String, Object> parametros) {
		byte[] resultado = null;
		File archivoDestino = crearTemporal();
		try (InputStream jasper = new ClassPathResource(jasperDocumento).getInputStream()) {

			JasperPrint jasperPrint = JasperFillManager.fillReport(jasper, parametros, new JREmptyDataSource());

			JasperExportManager.exportReportToPdfFile(jasperPrint, archivoDestino.getPath());
			resultado = Files.readAllBytes(archivoDestino.toPath());
			Files.delete(archivoDestino.toPath());
		} catch (Exception exception) {
			LOGGER.error(ERROR_GENERANDO_EL_DOCUMENTO, exception);
			throw new ExcepcionTecnica(ERROR_GENERANDO_EL_DOCUMENTO, exception);
		}
		return resultado;
	}

	private File crearTemporal() {
		File temporal = null;
		try {
			temporal = File.createTempFile(UUID.randomUUID().toString(), EXTENSION_PDF);
		} catch (IOException e) {
			LOGGER.error(ERROR_CREANDO_EL_DOCUMENTO, e);
			throw new ExcepcionTecnica(ERROR_CREANDO_EL_DOCUMENTO, e);
		}
		return temporal;
	}

}
