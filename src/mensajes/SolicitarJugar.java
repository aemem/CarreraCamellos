package mensajes;

public class SolicitarJugar extends Mensaje{
    // mensaje que envian los Camellos al Servidor para decir que quieren unirse a una carrera

    private int idCamello;

    public SolicitarJugar(int id) {
        tipoMsg = TipoMsg.SOLICITAR_JUGAR;
        this.idCamello = id;
    }

    public int getIdCamello() {
        return idCamello;
    }

}