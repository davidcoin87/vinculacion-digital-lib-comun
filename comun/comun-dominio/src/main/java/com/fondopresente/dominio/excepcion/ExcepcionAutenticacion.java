package com.fondopresente.dominio.excepcion;

public class ExcepcionAutenticacion extends RuntimeException {

	public static final String MENSAJE_CREDENCIALES_ERRONEAS = "Las credenciales con las que esta intentando acceder no existen";
	private static final long serialVersionUID = 1L;

	public ExcepcionAutenticacion(String message) {
		super(message);
	}

}
