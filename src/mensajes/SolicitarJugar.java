package mensajes;

public class SolicitarJugar extends Mensaje{
    // mensaje que envian los Camellos al Servidor para decir que quieren unirse a una carrera

    public String unirse;

    public SolicitarJugar(){
        this.tipoMsg = tipoMsg.SOLICITAR_JUGAR;
    }

    public SolicitarJugar(String unirse){

    }

}
