package mensajes;

public class FinCarrera extends Mensaje{

    // mensaje que envia el servidor a los camellos de un grupo para indicar que ha acabado la carrera

    public String idGrupo;


    public FinCarrera(){
        this.tipoMsg = tipoMsg.FIN_CARRERA;
    }

    public FinCarrera(String idGrupo){
        this.idGrupo = idGrupo;
    }

    public String getIdGrupo() {
        return idGrupo;
    }

    @Override
    public String toString() {
        return "FinCarrera{" +
                "idGrupo='" + idGrupo + '\'' +
                ", tipoMsg=" + tipoMsg +
                '}';
    }
}
