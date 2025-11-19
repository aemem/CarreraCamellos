package mensajes;

public class EventoCarrera extends Mensaje {

    // mensaje que indica un evento de carrera: salida (lo envia servidor para empezar la carrera), paso (el camello avanza 1-3 pasos),
    // caida (error), meta (lo envia un camello cuando llega a la meta)

    public TipoEvento tipoEvento;
    public int idEmisor;
    private int pasos;
    // añadir timestamp


    public EventoCarrera(int id, TipoEvento tipoEvento) {
        super();
        this.idEmisor = id;
        tipoMsg = TipoMsg.EVENTO_CARRERA;
        this.tipoEvento = tipoEvento;
    }

    public TipoEvento getTipoEvento() {
        return tipoEvento;
    }

    public int getIdEmisor() {
        return idEmisor;
    }
    public int getPasos()             { return pasos; }
    public void setPasos(int pasos)   { this.pasos = pasos; }
}