package carreraCamellos;

import mensajes.AsignarGrupo;
import mensajes.EventoCarrera;
import mensajes.SolicitarJugar;
import mensajes.TipoEvento;

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

    // Atributos comunicación
    private static final int PUERTO_TCP = 12345;
    private static String host;
    private Socket socket;
    private TCPunicast tcp;

    private String ipMulti;
    private int puertoMulti;
    private Thread listenerMulti;
    private boolean escuchandoMulti = false;

    // Atributos carrera
    private int idCamello;
    private int posicion = 0;
    private final int meta = 100;

    public Camello(int idCamello) throws IOException {
        this.idCamello = idCamello;

    }

    public int getIdCamello() {
        return idCamello;
    }

    public int getPosicion() {
        return posicion;
    }

    private void conectarTCP() throws IOException{
        socket = new Socket(host, PUERTO_TCP);
        tcp = new TCPunicast(socket);
    }

    // Enviad solicitud de juego al servidor
    public void enviarSolicitud() throws IOException {
        System.out.println("Enviando solicitud de juego...");
        tcp.enviar(new SolicitarJugar(idCamello));
    }

    public void listenerMulticast(String ipMulti, int puerto) throws IOException {
        InetAddress grupo = InetAddress.getByName(ipMulti);
        UDPmulticast udp = new UDPmulticast(grupo, puerto);
        udp.socket = new MulticastSocket(puerto);
        udp.socket.joinGroup(grupo);

        escuchandoMulti = true;

        listenerMulti = new Thread(() -> {
            try {
                while (escuchandoMulti) {
                    EventoCarrera ev = udp.recibir();
                    procesarEvento(ev);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    udp.socket.leaveGroup(grupo);
                } catch (IOException ignored) {
                }
                udp.socket.close();
            }
        }, "CamelloListener");
        listenerMulti.start();
    }

    private void procesarEvento(EventoCarrera ev) {
        switch (ev.getTipoEvento()) {
            case SALIDA:
                System.out.println("SALIDA recibido");
                break;
            case PASO:
                avanzar();
                break;
            case META:
                escuchandoMulti = false;
                System.out.format("El camello %d ha llegado a la meta", idCamello);
                break;
            case FIN:
                escuchandoMulti = false;
                System.out.println("Mensaje FIN recibido");
                break;
            case CAIDA:
                System.out.format("El camello %d se ha caído de la carrera", ev.idEmisor);
                break;
            default:
                System.out.println("Evento inválido");
        }
    }


    public void unirseGrupo() throws IOException, ClassNotFoundException {

        AsignarGrupo ag = (AsignarGrupo) tcp.recibir();
        System.out.println("Recibiendo asignacion de grupo...");
        if (ag instanceof AsignarGrupo) {
            // Unirse al multicast y escuchar
            this.ipMulti = ag.ipMulti;
            this.puertoMulti = ag.puerto;
            listenerMulticast(ag.ipMulti, ag.puerto);
        } else {
            System.out.println("Error al intentar unirse al grupo");
        }
    }

    private void avanzar() {
        try {
            UDPmulticast udp = new UDPmulticast();
            udp.socket = new MulticastSocket();
            udp.ipMulticast = InetAddress.getByName(ipMulti);
            udp.puerto = puertoMulti;

            int pasos = (int) (Math.random() * 3) + 1;
            posicion += pasos;
            EventoCarrera paso = new EventoCarrera(idCamello, TipoEvento.PASO);
            paso.setPasos(pasos);

            udp.enviar(paso);

            if (posicion >= meta) {

                udp.enviar(new EventoCarrera(idCamello, TipoEvento.META));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void escucharMensajesCaida() {
        try {
            InetAddress grupo = InetAddress.getByName(ipMulti);
            UDPmulticast udp = new UDPmulticast(grupo, puertoMulti);
            udp.socket = new MulticastSocket(puertoMulti);
            udp.socket.joinGroup(grupo);
            while (true) {
                EventoCarrera mensaje = udp.recibir();
                if (mensaje.getTipoEvento() == CAIDA) {
                    System.out.println("Camello " + idCamello + " recibió mensaje CAIDA - Eliminándose de la carrera");

                    // Crear mensaje de notificación
                    EventoCarrera aviso = new EventoCarrera(idCamello, CAIDA);

                    // ► Enviar el mensaje al multicast
                    udp.enviar(aviso);

                    // Eliminar al camello
                    System.exit(0);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error escuchando mensajes CAIDA: " + e.getMessage());
        }
    }

    public static void main(String[] args) {

        if (args.length < 1) {
            System.out.println("Faltan parámetros");
        }

        host = args[0];

        try {
            System.out.println("Creando camello...");
            int id = (int) (Math.random() * 250);
            Camello camello = new Camello(id);
            camello.conectarTCP();
            new Thread(() -> {
                try {
                    camello.enviarSolicitud();
                    camello.unirseGrupo();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

