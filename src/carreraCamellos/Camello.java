package carreraCamellos;

import mensajes.AsignarGrupo;
import mensajes.EventoCarrera;
import mensajes.SolicitarJugar;
import mensajes.TipoEvento;

import javax.swing.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class Camello {

    private static final int PUERTO_TCP = 12345;
    private static String hostServidor;

    private int idCamello;
    private Socket socketTCP;
    private TCPunicast tcp;

    private Carrera carrera; // GUI de la carrera

    public Camello(int idCamello) throws IOException {
        this.idCamello = idCamello;
        this.socketTCP = new Socket(hostServidor, PUERTO_TCP);
        this.tcp = new TCPunicast(socketTCP);
    }

    // Enviar solicitud de unirse a una carrera
    public void solicitarJugar() throws IOException {
        System.out.println("Camello " + idCamello + ": enviando solicitud de juego...");
        tcp.enviar(new SolicitarJugar(idCamello));
    }

    // Recibir asignación de grupo del servidor
    public void recibirAsignacion() throws IOException, ClassNotFoundException {
        AsignarGrupo msg = (AsignarGrupo) tcp.recibir();
        if (!(msg instanceof AsignarGrupo)) {
            System.out.println("Error: mensaje inesperado del servidor");
            return;
        }
        AsignarGrupo ag = (AsignarGrupo) msg;
        System.out.println("Recibida asignación de grupo: " + ag.getIdCarrera() +
                ", IP: " + ag.getIpMulti() + ", puerto: " + ag.getPuerto());

        // Crear la interfaz de carrera local
        carrera = new Carrera(ag.getIdCarrera(), InetAddress.getByName(ag.getIpMulti()), ag.getPuerto(), idCamello);
        carrera.agregarCamello(idCamello); // añadir el propio camello

        // Arrancar hilo para escuchar eventos multicast
        new Thread(carrera, "CarreraListener-" + idCamello).start();
    }

    // Permite agregar los demás camellos cuando llegan
    public void agregarCamello(int idOtroCamello) {
        if (carrera != null) {
            carrera.agregarCamello(idOtroCamello);
        }
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Uso: java Camello <host_servidor>");
            return;
        }
        hostServidor = args[0];

        try {
            int id = (int) (Math.random() * 1000);
            Camello camello = new Camello(id);

            camello.solicitarJugar();
            camello.recibirAsignacion();

            System.out.println("Camello " + id + " listo para correr.");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
