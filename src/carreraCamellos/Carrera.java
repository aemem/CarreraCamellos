package carreraCamellos;

import mensajes.EventoCarrera;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.MulticastSocket;
import java.util.List;
import java.util.ArrayList;
import java.net.InetAddress;

public class Carrera extends JFrame implements Runnable {
    // atributos de la interfaz
    private static final Color COLORS[] = {Color.GREEN, Color.RED, Color.YELLOW, Color.CYAN};
    private static final int FINISH_LINE_X = 100;
    private static final int TRACK_START_X = 105;
    private static final int CAMEL_ICON_SIZE = 40;
    private ArrayList<JLabel> camelLabels;
    private JButton buttonRun;
    private JPanel bgPanel;
    private JLabel labelWinner;
    private JLabel statusBar;

    // atributos de la carrera
    private boolean carreraTerminada;
    private static final int numCamellos = 4;
    private int idCarrera;
    private List<Camello> camellos;
    private InetAddress ipGrupo; // dirección multicast del grupo
    private int puerto;          // puerto multicast que va a usar la carrera

    // Constructor
    public Carrera(int idCarrera, InetAddress ipGrupo, int puerto) {
        this.ipGrupo = ipGrupo;
        this.puerto = puerto;
        this.idCarrera = idCarrera;
        this.camellos = new ArrayList<>();
        camelLabels = new ArrayList<>();

        setTitle("Carrera #" + idCarrera);
        setPreferredSize(new Dimension(800, 700));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        setContentPane(bgPanel);

        crearInterfaceUI();
    }

    // getters
    public int getIdCarrera() {
        return idCarrera;
    }

    public InetAddress getIpGrupo() {
        return ipGrupo;
    }

    public int getPuerto() {
        return puerto;
    }

    public void setCarreraTerminada(boolean carreraTerminada) {
        this.carreraTerminada = carreraTerminada;
    }

    // metodos interfaz
    private void crearInterfaceUI() {
        bgPanel.setBackground(new Color(240, 240, 240));
        bgPanel.setLayout(null);
        bgPanel.add(labelWinner);
        bgPanel.add(buttonRun);
        bgPanel.add(statusBar);

        // carriles y meta
        bgPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // draw horizontal lanes
                g.setColor(new Color(220, 220, 220));
                for (int i = 0; i <= 4; i++) {
                    int y = 70 + i * 80;
                    g.drawLine(0, y, getWidth(), y);
                }
                // draw the black finish line
                g.setColor(Color.BLACK);
                g.fillRect(FINISH_LINE_X, 70, 5, 80 * 4);

                // draw rotated “META” label
                Graphics2D g2d = (Graphics2D) g;
                g2d.setFont(new Font("Arial", Font.BOLD, 20));
                g2d.setColor(Color.BLACK);
                g2d.rotate(-Math.PI / 2);
                g2d.drawString("META", -70 - 80 * 4 / 2, FINISH_LINE_X + 22);
                g2d.rotate(Math.PI / 2);
            }
        };
        setContentPane(bgPanel);

        // ganador
        labelWinner.setFont(new Font("Arial", Font.BOLD, 22));
        labelWinner.setForeground(Color.BLACK);
        labelWinner.setBounds(0, 20, 800, 30);
        bgPanel.add(labelWinner);

        // boton
        buttonRun.setBounds(340, 610, 120, 40);
        buttonRun.addActionListener(e -> {
            buttonRun.setEnabled(false);
            labelWinner.setText("");
            statusBar.setText("¡Carrera en marcha! [ID: " + idCarrera +
                    ", IP: " + ipGrupo.getHostAddress() +
                    ", Puerto: " + puerto + "]");
            // The UI thread is already visible; the race logic runs in a separate thread.
            new Thread(this, "Carrera-Thread-" + idCarrera).start();
        });
        bgPanel.add(buttonRun);

        // barra de estado
        statusBar.setFont(new Font("Arial", Font.ITALIC, 15));
        statusBar.setForeground(Color.DARK_GRAY);
        statusBar.setBounds(10, 655, 780, 25);
        bgPanel.add(statusBar);

    }

    // metodos carrera

    public boolean estaLlena() {
        return camellos.size() >= 4;
    }

    public void agregarCamello(int idCamello) throws IOException {
        Camello camello = new Camello(idCamello);
        camellos.add(camello);
        JLabel lbl = new JLabel("\uD83D\uDC2B", SwingConstants.CENTER); // fallback emoji
        lbl.setFont(new Font("Dialog", Font.PLAIN, 34));
        lbl.setForeground(COLORS[(camellos.size() - 1) % COLORS.length]);
        lbl.setBounds(TRACK_START_X,
                70 + (camellos.size() - 1) * 80,
                CAMEL_ICON_SIZE,
                CAMEL_ICON_SIZE);
        camelLabels.add(lbl);
        bgPanel.add(lbl);
        bgPanel.revalidate();
        bgPanel.repaint();
    }

    private void moverCamello(int pasos, int idCamello) throws IOException {
        int indice = 0;
        // busca el camello con el id recibido
        Camello cam = null;
        for (int i = 0; i < camellos.size(); i++) {
            if (camellos.get(i).getIdCamello() == idCamello) {
                cam = camellos.get(i);
                indice = i;
            }
        }
        // actualiza sus pasos y los muestra en la interfaz
        int pos = cam.getPosicion() + pasos;

        camelLabels.get(indice).setBounds(105 + cam.getPosicion(), 70 + indice * 80, 40, 40);
        if (pos >= FINISH_LINE_X && !carreraTerminada) {
            setCarreraTerminada(true);
        }
    }

    public boolean contieneCamello(int idCamello) {
        for (Camello camello : camellos) {
            if (camello.getIdCamello() == idCamello) {
                return true;
            }
        }
        return false;
    }


    public void eliminarCamello(int idCamello) {
        for (int i = 0; i < camellos.size(); i++) {
            if (camellos.get(i).getIdCamello() == idCamello) {
                camellos.remove(i);
                // También eliminar de la interfaz si es necesario
                if (i < camelLabels.size()) {
                    bgPanel.remove(camelLabels.get(i));
                    camelLabels.remove(i);
                    bgPanel.repaint();
                }
                System.out.println("Camello " + idCamello + " eliminado de la carrera " + idCarrera);
                break;
            }
        }
    }

    @Override
    public void run() {
        carreraTerminada = false;

        try (MulticastSocket ms = new MulticastSocket(puerto)) {
            // Unirse al grupo multicast
            ms.joinGroup(ipGrupo);

            // Enviar SALIDA
            UDPmulticast udp = new UDPmulticast(ipGrupo, puerto);
            udp.socket = ms;

            // Recibir eventos
            while (!carreraTerminada) {
                EventoCarrera ev = udp.recibir();

                switch (ev.getTipoEvento()){
                    case SALIDA:
                        SwingUtilities.invokeLater(() ->
                                statusBar.setText("¡Carrera iniciada!"));
                        break;
                    case PASO:
                        int idEmisor = ev.getIdEmisor();
                        int pasos    = ev.getPasos();
                        moverCamello(pasos, idEmisor);
                        break;
                    case META:
                        carreraTerminada = true;
                        break;
                    case FIN:
                        carreraTerminada = true;
                        SwingUtilities.invokeLater(() -> {
                            statusBar.setText("Fin de la carrera");
                            labelWinner.setText("Ganador: Camello " + ev.getIdEmisor());
                            buttonRun.setEnabled(true);
                            buttonRun.setText("Nueva carrera");
                        });
                        break;
                    case CAIDA:
                        SwingUtilities.invokeLater(() ->
                                statusBar.setText("Camello " + ev.getIdEmisor() + " cayó"));
                        break;
                    default:
                        System.out.println("Evento desconocido");
                }

            }

        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
            SwingUtilities.invokeLater(() -> {
                buttonRun.setEnabled(true);
                buttonRun.setText("Nueva carrera");
                statusBar.setText("Carrera finalizada [ID: " + idCarrera +
                        ", IP: " + ipGrupo.getHostAddress() +
                        ", Puerto: " + puerto + "] Pulsa para nueva partida.");
            });

        }
    }
}