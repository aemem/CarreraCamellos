package mensajes;

import java.net.InetAddress;

/**
 * Mensaje que envía el servidor a los camellos con su ID de grupo
 * y la dirección IP multicast correspondiente.
 */
public class AsignarGrupo extends Mensaje {

    public int idCarrera;
    public String ipMulti;
    public int puerto;

    public AsignarGrupo(int id, int idCarrera, String ipMulti, int puerto) {
        super(id);
        tipoMsg = TipoMsg.ASIGNAR_GRUPO;
        this.idCarrera = idCarrera;
        this.ipMulti = ipMulti;
        this.puerto = puerto;
    }

    public int getIdCarrera() {
        return idCarrera;
    }

    public void setIdCarrera(int idCarrera) {
        this.idCarrera = idCarrera;
    }

}