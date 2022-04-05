package com.fondopresente.infraestructura.leer_template;

import com.fondopresente.infraestructura.excepcion.ExcepcionTecnica;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Template {
    private static final String ERROR_LEYENDO_EL_TEMPLATE = "Error al leer el template del correo";
    private static final Logger LOGGER = LoggerFactory.getLogger(Template.class);

    private Template() {
    }

    public static String obtener(String path) {
        try (InputStream template = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
             BufferedReader reader = new BufferedReader(new InputStreamReader(template, StandardCharsets.UTF_8))) {

            StringBuilder resultado = new StringBuilder();
            String linea;
            while ((linea = reader.readLine()) != null) {
                resultado.append(linea);
            }
            return resultado.toString();
        } catch (Exception e) {
            LOGGER.error(ERROR_LEYENDO_EL_TEMPLATE, e);
            throw new ExcepcionTecnica(ERROR_LEYENDO_EL_TEMPLATE, e);
        }
    }
}
