package mensajes;

import java.io.Serializable;

public abstract class Mensaje implements Serializable {

    public TipoMsg tipoMsg; // enum que indica el tipo de mensaje que es

    public Mensaje(int idCamello){}

    public TipoMsg getTipoMsg(){
        return tipoMsg;
    }
    public Mensaje() {}
}

