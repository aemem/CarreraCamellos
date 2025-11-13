package mensajes;

public class EventoCarrera extends Mensaje {

    // mensaje que indica un evento de carrera: salida (lo envia servidor para empezar la carrera), paso (el camello avanza 1-3 pasos),
    // caida (error), meta (lo envia un camello cuando llega a la meta)

    public TipoEvento tipoEvento;

    public EventoCarrera(){
        this.tipoMsg = tipoMsg.EVENTO_CARRERA;
    }

    public TipoEvento getTipoEvento() {
        return tipoEvento;
    }
}
