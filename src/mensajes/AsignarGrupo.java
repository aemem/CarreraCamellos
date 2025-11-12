package mensajes;

/**
 * Mensaje que envía el servidor a los camellos con su ID de grupo
 * y la dirección IP multicast correspondiente.
 */
public class AsignarGrupo extends Mensaje {

    public int idGrupo;
    public String ipMulticast;

    public AsignarGrupo() {
        this.tipoMsg = TipoMsg.ASIGNAR_GRUPO;
    }

    public AsignarGrupo(int idGrupo, String ipMulticast) {
        this.idGrupo = idGrupo;
        this.ipMulticast = ipMulticast;
    }

    public int getIdGrupo() {
        return idGrupo;
    }

    public String getIpMulticast() {
        return ipMulticast;
    }

    @Override
    public String toString() {
        return "AsignarGrupo{" +
                "idGrupo=" + idGrupo +
                ", ipMulticast='" + ipMulticast + '\'' +
                '}';
    }
}
