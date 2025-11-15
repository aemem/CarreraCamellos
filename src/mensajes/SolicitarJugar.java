package mensajes;

public class SolicitarJugar extends Mensaje{
    // mensaje que envian los Camellos al Servidor para decir que quieren unirse a una carrera

    public int idCamello;

    public SolicitarJugar(int idCamello) {
        this.tipoMsg = tipoMsg.SOLICITAR_JUGAR;
        this.idCamello = idCamello;
    }

    public int getIdCamello() {
        return idCamello;
    }

    public void setIdCamello(int idCamello) {
        this.idCamello = idCamello;
    }

}
