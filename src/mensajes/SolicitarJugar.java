package mensajes;

public class SolicitarJugar extends Mensaje{
    // mensaje que envian los Camellos al Servidor para decir que quieren unirse a una carrera


    public SolicitarJugar(int id) {
        super(id);
        tipoMsg = TipoMsg.SOLICITAR_JUGAR;
    }

    public int getIdCamello() {
        return id;
    }

    public void setIdCamello(int idCamello) {
        this.id = idCamello;
    }

}