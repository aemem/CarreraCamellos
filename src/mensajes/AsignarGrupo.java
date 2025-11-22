package mensajes;


public class AsignarGrupo extends Mensaje {

    public int idCarrera;
    public String ipMulti;
    public int puerto;

    public AsignarGrupo(int idCarrera, String ipMulti, int puerto) {
        tipoMsg = TipoMsg.ASIGNAR_GRUPO;
        this.idCarrera = idCarrera;
        this.ipMulti = ipMulti;
        this.puerto = puerto;
    }

    public int getIdCarrera() {
        return idCarrera;
    }

    public String getIpMulti() {
        return ipMulti;
    }

    public int getPuerto() {
        return puerto;
    }
}