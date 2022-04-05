package com.fondopresente.dominio.excepcion;

public class ExcepcionAutorizacion extends RuntimeException {

	public static final String MENSAJE_PERFIL_INVALIDO = "El usuario no tiene permiso para realizar esta petición.";
	private static final long serialVersionUID = 1L;

	public ExcepcionAutorizacion(String message) {
		super(message);
	}

}
