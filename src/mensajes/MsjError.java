package mensajes;

import static mensajes.TipoMsg.ERROR;

public class MsjError extends Mensaje {

    public TipoError tipoError;

    public MsjError(TipoError tipoError) {
        tipoMsg = ERROR;
        this.tipoError = tipoError;
    }

    public TipoError getTipoError() {
        return tipoError;
    }

    public void setTipoError(TipoError tipoError) {
        this.tipoError = tipoError;
    }

    public String getMensaje() {
        return this.tipoError.getMensaje();
    }
}
enum TipoError {
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