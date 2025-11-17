package carreraCamellos;

import mensajes.AsignarGrupo;
import mensajes.EventoCarrera;
import mensajes.SolicitarJugar;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;

import static mensajes.TipoEvento.*;

public class Camello {

    // Cliente. Cada camello tiene una carrera en la que participa.
    // Recibe mensajes del servidor (AsignarGrupo, EventoCarrera, FinCarrera)
    // Envia mensajes a otros camellos (EventoCarrera)
    // Envia mensajes al servidor (Error, EventoCarrera)

    private static final int PUERTO_TCP = 12345;
    private String host = "localhost";
    private int idCamello;
    private Socket socket;
    private int posicion = 0;
    private final int meta = 100;


    public Camello(int idCamello) throws IOException {
        Socket socket = new Socket(host, PUERTO_TCP);
    }

    public int getIdCamello() {
        return idCamello;
    }

    public int getPosicion() {
        return posicion;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }

    public int getMeta() {
        return meta;
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

            // AÑADIR: Escuchar mensajes de CAIDA en segundo plano
            new Thread(() -> escucharMensajesCaida(udp)).start();

        }else{
            System.out.println("Error al intentar unirse al grupo");
        }
    }

    // AÑADIR ESTE MÉTODO NUEVO
    private void escucharMensajesCaida(UDPmulticast udp) {
        try {
            while (true) {
                EventoCarrera mensaje = (EventoCarrera) udp.recibir();
                if (mensaje.getTipoEvento() == CAIDA) {
                    System.out.println("Camello " + idCamello + " recibió mensaje CAIDA - Eliminándose de la carrera");
                    // Eliminar el camello de la carrera
                    System.exit(0); // O tu lógica para eliminar el camello
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error escuchando mensajes CAIDA: " + e.getMessage());
        }
    }

    public void gestionCarrera(Carrera carrera) throws IOException {
        UDPmulticast udp = new UDPmulticast(carrera.getIpGrupo(), carrera.getPuerto());
        while (posicion < meta){
            int pasos = generarPasos();
            EventoCarrera evPasos = new EventoCarrera(PASO, pasos, idCamello);
            udp.enviar(evPasos);
        }
        EventoCarrera evMeta = new EventoCarrera(META, idCamello);
        udp.enviar(evMeta);
    }

    public void reset() {
        this.posicion = 0;
    }

    public static void main(String[] args){

    }
}