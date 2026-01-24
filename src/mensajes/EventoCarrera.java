package mensajes;

import java.util.List;

public class EventoCarrera extends Mensaje {

    public TipoEvento tipoEvento;
    public int idEmisor;
    private int pasos;
    public List<Integer> listaCamellos;


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
    public void setListaCamellos(List<Integer> lista) { this.listaCamellos = lista; }
    public List<Integer> getListaCamellos() { return listaCamellos; }

}

enum TipoEvento {
    SALIDA,
    PASO,
    CAIDA,
    META,
    FIN
}