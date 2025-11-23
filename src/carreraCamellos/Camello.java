package carreraCamellos;

import mensajes.AsignarGrupo;
import mensajes.SolicitarJugar;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;

public class Camello {
    // atributos
    private static final int PUERTO_TCP = 12345;
    private static String host;

    private int idCamello;
    private Socket socketTCP;
    private TCPunicast tcp;
    private Carrera carrera; // GUI de la carrera

    // constructor
    public Camello(int idCamello) throws IOException {
        this.idCamello = idCamello;
        this.socketTCP = new Socket(host, PUERTO_TCP);
        this.tcp = new TCPunicast(socketTCP);
    }

    // Enviar solicitud de unirse a una carrera
    public void solicitarJugar() throws IOException {
        System.out.println("Camello " + idCamello + ": enviando solicitud de juego...");
        tcp.enviar(new SolicitarJugar(idCamello));
    }

    // Recibir asignación de grupo del servidor
    public void recibirAsignacion() throws IOException, ClassNotFoundException {
        AsignarGrupo ag = (AsignarGrupo) tcp.recibir();
        System.out.println("Recibida asignación de grupo: " + ag.getIdCarrera() +
                ", IP: " + ag.getIpMulti() + ", puerto: " + ag.getPuerto());

        InetAddress ip = InetAddress.getByName(ag.getIpMulti());
        int puerto = ag.getPuerto();
        MulticastSocket ms = new MulticastSocket(puerto);
        ms.joinGroup(ip);
        // Crear la interfaz de carrera local
        carrera = new Carrera(ag.getIdCarrera(), ip, puerto, idCamello, ms);
        carrera.getCamellos().add(idCamello);
        // Arrancar hilo para escuchar eventos multicast
        new Thread(carrera, "CarreraListener-" + idCamello).start();
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Falta ip del servidor");
            return;
        }
        host = args[0];

        try {
            int id = (int) (Math.random() * 200);
            Camello camello = new Camello(id);

            camello.solicitarJugar();
            camello.recibirAsignacion();

            System.out.println("Camello " + id + " listo para correr.");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
