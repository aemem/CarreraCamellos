package mensajes;

import java.io.Serializable;

public abstract class Mensaje implements Serializable {
    // clase abstracta de la que heredan todos los demás mensajes

    public TipoMsg tipoMsg; // enum que indica el tipo de mensaje que es

    public Mensaje(int idCamello){}

    public TipoMsg getTipoMsg(){
        return tipoMsg;
    }
    public Mensaje() {}
}