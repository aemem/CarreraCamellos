package mensajes;

public class Error extends Mensaje {

    // Mensajes de error. Hay que decidir que tipos de mensaje va a haber
    //asdadasdasd

    public TipoEvento tipoEvento;

    public Error () {
        this.tipoMsg = tipoMsg.ERROR;
    }

    public TipoEvento getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(TipoEvento tipoEvento) {
        this.tipoEvento = tipoEvento;
    }
}
