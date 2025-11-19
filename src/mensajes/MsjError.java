package mensajes;

import static mensajes.TipoMsg.ERROR;

public class MsjError extends Mensaje {

    // Mensajes de error. Hay que decidir que tipos de mensaje va a haber
    //asdadasdasd

    public TipoError tipoError;

    public MsjError(int id, TipoError tipoError) {
        super(id);
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