package mensajes;

public enum TipoError {
    PAQUETE("HOLA") ,
    DIRECTORIO("Directorio"),
    DEFAULT("ERROR no especificado"),
    DESCONEXION("Se desconecto un cliente");
    private final String mensaje;

    TipoError(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getMensaje() {
        return mensaje;
    }
}
