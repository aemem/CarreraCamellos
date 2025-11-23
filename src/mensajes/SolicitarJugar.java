package mensajes;

public class SolicitarJugar extends Mensaje{

    private int idCamello;

    public SolicitarJugar(int id) {
        tipoMsg = TipoMsg.SOLICITAR_JUGAR;
        this.idCamello = id;
    }

    public int getIdCamello() {
        return idCamello;
    }

}