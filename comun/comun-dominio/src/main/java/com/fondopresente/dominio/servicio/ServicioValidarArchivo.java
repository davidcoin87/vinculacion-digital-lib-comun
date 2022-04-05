package com.fondopresente.dominio.servicio;

import java.io.IOException;
import java.util.Map;

public interface ServicioValidarArchivo {
    void validarEstructura(byte[] archivo, Map<String, String> camposArchivo) throws IOException;

}
