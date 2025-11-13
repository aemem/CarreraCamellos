package mensajes;

/**
 * Mensaje que envía el servidor a los camellos con su ID de grupo
 * y la dirección IP multicast correspondiente.
 */
public class AsignarGrupo extends Mensaje {

    public TipoEvento tipoEvento;

    public AsignarGrupo() {
        this.tipoMsg = TipoMsg.ASIGNAR_GRUPO;
    }

    public TipoEvento getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(TipoEvento tipoEvento) {
        this.tipoEvento = tipoEvento;
    }
}
