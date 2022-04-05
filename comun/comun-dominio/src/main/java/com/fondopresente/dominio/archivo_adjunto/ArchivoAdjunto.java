package com.fondopresente.dominio.archivo_adjunto;

public final class ArchivoAdjunto {
    private final String nombreArchivoAdjunto;
    private final byte[] archivoAdjunto;

    public ArchivoAdjunto(String nombreArchivoAdjunto, byte[] archivoAdjunto) {
        this.nombreArchivoAdjunto = nombreArchivoAdjunto;
        this.archivoAdjunto = archivoAdjunto;
    }

    public String getNombreArchivoAdjunto() {
        return nombreArchivoAdjunto;
    }

    public byte[] getArchivoAdjunto() {
        return archivoAdjunto;
    }
}
