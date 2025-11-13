package carreraCamellos;



import mensajes.*;

import java.net.InetAddress;

public class UDPmulticast {

    public void enviar(Mensaje msg){

    }

    public Mensaje recibir(){
        EventoCarrera msg = new EventoCarrera();
        return msg;
    }
}
