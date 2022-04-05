package com.fondopresente.dominio.excepcion;

public class ExcepcionDeEmail extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ExcepcionDeEmail(String mensaje) {
        super(mensaje);
    }

}
