package carreraCamellos;

import mensajes.AsignarGrupo;
import mensajes.EventoCarrera;
import mensajes.SolicitarJugar;
import mensajes.TipoEvento;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.URL;

import static javax.swing.BorderFactory.createEmptyBorder;
import static mensajes.TipoEvento.*;

public class Camello extends JFrame {

    // Cliente. Cada camello tiene una carrera en la que participa.
    // Recibe mensajes del servidor (AsignarGrupo, EventoCarrera, FinCarrera)
    // Envia mensajes a otros camellos (EventoCarrera)
    // Envia mensajes al servidor (Error, EventoCarrera)

    // Atributos comunicación
    private static final int PUERTO_TCP = 12345;
    private String host = "localhost";
    private Socket socket;
    private TCPunicast tcp;

    private String ipMulti;   // keep for sending META
    private int puertoMulti;
    Thread listener;
    private boolean escuchandoMulti = false;

    // Atributos carrera
    private int idCamello;
    private int posicion = 0;
    private final int meta = 100;


    // Atributos interfaz
    private final JLabel camelLabel;
    private final JLabel statusLabel;


    public Camello(int idCamello) throws IOException {
        this.idCamello = idCamello;

        setTitle("Camello: " + idCamello);
        setSize(500,200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        camelLabel = new JLabel(); // añadir imagen camello
        camelLabel.setHorizontalAlignment(SwingConstants.LEFT);
        camelLabel.setBorder(BorderFactory.createEmptyBorder());
        add(camelLabel, BorderLayout.CENTER);

        ImageIcon icon = loadCamelIcon(40, 40);
        if (icon != null) {
            camelLabel.setIcon(icon);
        } else {
            camelLabel.setText("\\uD83D\\uDC2B");
        }

        statusLabel = new JLabel("Inicializando...", SwingConstants.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);

        this.socket = new Socket(host, PUERTO_TCP);
        this.tcp = new TCPunicast(socket);
    }

    public int getIdCamello() {
        return idCamello;
    }

    public int getPosicion() {
        return posicion;
    }

    private ImageIcon loadCamelIcon(int w, int h){
        URL url = getClass().getResource("camel.png");
        if (url == null){
            System.out.println("No se econtró camel.png");
            return null;
        }
        Image img = new ImageIcon(url).getImage().getScaledInstance(w,h, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    // Enviad solicitud de juego al servidor
    public void enviarSolicitud() throws IOException{
        System.out.println("Enviando solicitud de juego...");
        SolicitarJugar solicitud = new SolicitarJugar(idCamello);
        tcp.enviar(solicitud);
    }

    public void listenerMulticast(String ipMulti, int puerto) throws IOException{
        InetAddress grupo = InetAddress.getByName(ipMulti);
        UDPmulticast udp = new UDPmulticast(grupo,puerto);
        udp.socket = new MulticastSocket(puerto);
        udp.socket.joinGroup(grupo);

        escuchandoMulti = true;

        listener = new Thread(() -> {
            try{
                while (escuchandoMulti){
                    EventoCarrera ev = udp.recibir();
                    procesarEvento(ev);
                }
            }catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try { udp.socket.leaveGroup(grupo); } catch (IOException ignored) {}
                udp.socket.close();
            }
        }, "CamelloListener");
        listener.start();
    }

    private void procesarEvento(EventoCarrera ev){
        switch (ev.getTipoEvento()){
            case SALIDA:
                SwingUtilities.invokeLater(() ->
                        statusLabel.setText("¡Carrera iniciada!"));
                break;
            case PASO:
                avanzar();
                break;
            case META:
                escuchandoMulti = false;
                SwingUtilities.invokeLater(() -> statusLabel.setText(String.format("El camello %d ha llegado a la meta", idCamello)));
                break;
            case FIN:
                escuchandoMulti = false;
                SwingUtilities.invokeLater(() ->{
                    statusLabel.setText("Fin de la carrera");
                    JOptionPane.showConfirmDialog(Camello.this, "La carrera ha terminado", "Fin", JOptionPane.INFORMATION_MESSAGE);
                });
                break;
            case CAIDA:
                System.out.format("El camello %d se ha caído de la carrera", ev.idEmisor);
                SwingUtilities.invokeLater(() -> statusLabel.setText("Caída"));
                break;
            default:
                System.out.println("Evento inválido");
        }
    }


    public void unirseGrupo() throws IOException, ClassNotFoundException {
        statusLabel.setText("Esperando asignación de grupo...");

        AsignarGrupo ag = (AsignarGrupo) tcp.recibir();
        if(ag instanceof AsignarGrupo){

            SwingUtilities.invokeLater(() ->
                    statusLabel.setText(
                            String.format("Asignado a grupo %d – %s:%d", ag.getIdCarrera(),ag.ipMulti,ag.puerto)));

            // Unirse al multicast y escuchar
            this.ipMulti = ag.ipMulti;
            this.puertoMulti = ag.puerto;
            listenerMulticast(ag.ipMulti, ag.puerto);
        }else{
            System.out.println("Error al intentar unirse al grupo");
        }
    }

    private void avanzar(){
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


        SwingUtilities.invokeLater(() -> {
            camelLabel.setLocation(posicion + 20, camelLabel.getY());
            statusLabel.setText(String.format("Camello %d avanza %d pasos: ", idCamello, pasos));
        });

        if (posicion >= meta) {

            udp.enviar(new EventoCarrera(idCamello, TipoEvento.META));
        }
        }catch (IOException e) {
                e.printStackTrace();
            }
    }
    public void escucharMensajesCaida() {
        try {
            InetAddress grupo = InetAddress.getByName(ipMulti);
            UDPmulticast udp = new UDPmulticast(grupo,puertoMulti);
            udp.socket = new MulticastSocket(puertoMulti);
            udp.socket.joinGroup(grupo);
            while (true) {
                EventoCarrera mensaje = udp.recibir();
                if (mensaje.getTipoEvento() == CAIDA) {
                    System.out.println("Camello " + idCamello + " recibió mensaje CAIDA - Eliminándose de la carrera");

                    // Crear mensaje de notificación
                    EventoCarrera aviso = new EventoCarrera(idCamello,CAIDA);

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

    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> {
            try{
                int id = (int)(Math.random()*200);
                Camello camello = new Camello(id);
                camello.enviarSolicitud();
                camello.unirseGrupo();
            }catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(
                        null,
                        "Error al iniciar el cliente:\n" + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}

