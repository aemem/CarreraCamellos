package mensajes;

public class SolicitarJugar extends Mensaje{
    // mensaje que envian los Camellos al Servidor para decir que quieren unirse a una carrera

    public TipoEvento tipoEvento;

    public SolicitarJugar(){
        this.tipoMsg = tipoMsg.SOLICITAR_JUGAR;
    }

    public TipoEvento getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(TipoEvento tipoEvento) {
        this.tipoEvento = tipoEvento;
    }
}
