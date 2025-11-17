package mensajes;

/**
 * Mensaje que envía el servidor a los camellos con su ID de grupo
 * y la dirección IP multicast correspondiente.
 */
public class AsignarGrupo extends Mensaje {

    public int idCarrera;

    public AsignarGrupo(int idCarrera) {
        this.tipoMsg = TipoMsg.ASIGNAR_GRUPO;
        this.idCarrera = idCarrera;
    }

    public AsignarGrupo(int idGrupo, String ipMulti, int puerto) {
        super();
    }

    public int getIdCarrera() {
        return idCarrera;
    }

    public void setIdCarrera(int idCarrera) {
        this.idCarrera = idCarrera;
    }

}
