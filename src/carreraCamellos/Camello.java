package carreraCamellos;

import mensajes.AsignarGrupo;
import mensajes.EventoCarrera;
import mensajes.Mensaje;
import mensajes.SolicitarJugar;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;

public class Camello {

    // Cliente. Cada camello tiene una carrera en la que participa.
    // Recibe mensajes del servidor (AsignarGrupo, EventoCarrera, FinCarrera)
    // Envia mensajes a otros camellos (EventoCarrera)
    // Envia mensajes al servidor (Error, EventoCarrera)

    private static final int PUERTO_TCP = 12345;
    private String host = "localhost";
    private int idCamello;
    Socket socket;

    public Camello(int idCamello) throws IOException {
        Socket socket = new Socket(host, PUERTO_TCP);
    }

    TCPunicast tcp = new TCPunicast(socket);

    public void enviarSolicitud() throws IOException{
        System.out.println("Enviando solicitud de juego...");
        SolicitarJugar solicitud = new SolicitarJugar(idCamello); // El mensaje debe enviar la id del camello que solicita
        tcp.enviar(solicitud);
    }

    public void unirseGrupo() throws IOException, ClassNotFoundException {
        AsignarGrupo ag = (AsignarGrupo) tcp.recibir();
        if(ag instanceof AsignarGrupo){

            InetAddress ipMulticast = InetAddress.getByName(ag.ipMulti);
            UDPmulticast udp = new UDPmulticast(ipMulticast,ag.puerto);
            udp.socket = new MulticastSocket(ag.puerto);
            udp.socket.joinGroup(ipMulticast);

            System.out.println("El camello " + idCamello + " se ha unido a la carrera " + ag.idCarrera);
        }else{
            System.out.println("Error al intentar unirse al grupo");
        }
    }

    public void gestionCarrera(){
//        UDPmulticast udp = new UDPmulticast();
//        while (posicion < meta){
//            int pasos = generarPasos();
//            EventoCarrera evPasos = new EventoCarrera(PASO, pasos, idCamello);
//            udp.enviar(evPasos);
//        }
//        EventoCarrera evMeta = new EventoCarrera(META, idCamello);
//        udp.enviar(evMeta);
    }
}
