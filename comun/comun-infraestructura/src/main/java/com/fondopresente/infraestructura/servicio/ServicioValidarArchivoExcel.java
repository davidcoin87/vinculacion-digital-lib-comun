package com.fondopresente.infraestructura.servicio;

import com.fondopresente.dominio.excepcion.ExcepcionValorInvalido;
import com.fondopresente.dominio.servicio.ServicioValidarArchivo;
import com.fondopresente.infraestructura.excepcion.ExcepcionTecnica;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;


public class ServicioValidarArchivoExcel implements ServicioValidarArchivo {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServicioEnviarMailBase.class);
    private static final String ERROR_VALIDANDO_EL_ARCHIVO = "ERROR VALIDANDO EL ARCHIVO";

    private static final String ERROR_ARCHIVO_SIN_CAMPOS_NECESARIOS = "El archivo no tiene los campos requeridos para continuar el proceso";

    @Override
    public void validarEstructura(byte[] archivo, Map<String, String> camposArchivo) {
        try {
            InputStream entrada = new ByteArrayInputStream(archivo);
            Workbook libro = WorkbookFactory.create(entrada);
            Sheet hoja = libro.getSheetAt(0);
            Row fila = hoja.getRow(0);
            Iterator<Cell> columnas = fila.cellIterator();
            validarNombresEncabezados(camposArchivo, columnas);
        } catch (IOException e) {
            LOGGER.error(ERROR_VALIDANDO_EL_ARCHIVO, e);
            throw new ExcepcionTecnica(ERROR_VALIDANDO_EL_ARCHIVO, e);
        }

    }

    private void validarNombresEncabezados(Map<String, String> camposArchivo, Iterator<Cell> columnas) {
        int contadorDeColumnas =0;
        while (columnas.hasNext()) {
            Cell valor = columnas.next();
            if (camposArchivo.containsKey(valor.getStringCellValue())) {
                contadorDeColumnas++;
            }
        }
        validarCantidadDeColumnas(camposArchivo, contadorDeColumnas);
    }

    private void validarCantidadDeColumnas(Map<String, String> camposArchivo, int totalDeColumnas) {
        if (camposArchivo.size() != totalDeColumnas) {
            throw new ExcepcionValorInvalido(ERROR_ARCHIVO_SIN_CAMPOS_NECESARIOS);
        }
    }

}
