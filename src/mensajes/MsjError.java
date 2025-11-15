package mensajes;

public class MsjError extends Mensaje {

    // Mensajes de error. Hay que decidir que tipos de mensaje va a haber
    //asdadasdasd

    public TipoError tipoError;

    public MsjError(TipoError tipoError) {
        this.tipoMsg = tipoMsg.ERROR;
        this.tipoError = tipoError == null ? TipoError.DEFAULT: tipoError;

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