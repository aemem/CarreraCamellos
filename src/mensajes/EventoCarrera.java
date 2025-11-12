package mensajes;

public class EventoCarrera extends Mensaje {

    // mensaje que indica un evento de carrera: salida (lo envia servidor para empezar la carrera), paso (el camello avanza 1-3 pasos),
    // caida (error), meta (lo envia un camello cuando llega a la meta)

    public String salida;
    public String paso;
    public String caida;
    public String meta;
    public TipoEvento tipoEvento;

    public EventoCarrera(){
        this.tipoMsg = tipoMsg.EVENTO_CARRERA;
    }
    public EventoCarrera(String salida, String paso, String caida, String meta) {
        this.salida = salida;
        this.paso = paso;
        this.caida = caida;
        this.meta = meta;
    }

    public String getSalida() {
        return salida;
    }

    public String getCaida() {
        return caida;
    }

    public String getPaso() {
        return paso;
    }

    public String getMeta() {
        return meta;
    }

    public TipoEvento getTipoEvento() {
        return tipoEvento;
    }
}
