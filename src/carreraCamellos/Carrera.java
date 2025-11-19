package carreraCamellos;

import mensajes.EventoCarrera;
import mensajes.TipoEvento;

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
    private ArrayList<JLabel> camelLabels;
    private JButton buttonRun;
    private JPanel bgPanel;
    private JLabel labelWinner;
    private JLabel statusBar;

    // atributos de la carrera
    private boolean carreraLista;
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
        camelLabels = new ArrayList<>();
        crearInterfaceUI();
    }

    // getters
    public boolean isCarreraTerminada() {
        return carreraTerminada;
    }

    public void setCarreraTerminada(boolean estado) {
        this.carreraTerminada = estado;
    }

    public boolean isCarreraLista() {
        return carreraLista;
    }

    public void setCarreraLista(boolean carreraLista) {
        this.carreraLista = carreraLista;
    }

    public int getIdCarrera() {
        return idCarrera;
    }

    public InetAddress getIpGrupo() {
        return ipGrupo;
    }

    public int getPuerto() {
        return puerto;
    }

    // metodos interfaz
    private void crearInterfaceUI() {
        bgPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setBackground(new Color(240, 240, 240));
                g.setColor(new Color(220, 220, 220));
                for (int i = 0; i <= numCamellos; i++) {
                    int y = 70 + i * 80;
                    g.drawLine(0, y, getWidth(), y);
                }
                g.setColor(Color.BLACK);
                g.fillRect(FINISH_LINE_X, 70, 5, 80 * numCamellos);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setFont(new Font("Arial", Font.BOLD, 20));
                g2d.setColor(Color.BLACK);
                g2d.rotate(-Math.PI / 2);
                g2d.drawString("META", -70 - 80 * numCamellos / 2, FINISH_LINE_X + 22);
                g2d.rotate(Math.PI / 2);
            }
        };

        setContentPane(bgPanel);
        setTitle("Carrera de Camellos");
        setPreferredSize(new Dimension(800, 700));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);

        labelWinner = new JLabel("", JLabel.CENTER);
        labelWinner.setFont(new Font("Arial", Font.BOLD, 22));
        labelWinner.setForeground(Color.BLACK);
        labelWinner.setBounds(0, 20, 800, 30);
        bgPanel.add(labelWinner);

        buttonRun = new JButton("Run");
        buttonRun.setBounds(340, 610, 120, 40);
        bgPanel.add(buttonRun);

        statusBar = new JLabel("Listos para iniciar...");
        statusBar.setFont(new Font("Arial", Font.ITALIC, 15));
        statusBar.setForeground(Color.DARK_GRAY);
        statusBar.setBounds(10, 655, 780, 25);
        bgPanel.add(statusBar);

        for (int i = 0; i < camellos.size(); i++) {
            Camello camello = camellos.get(i);
            JLabel lblNombre = new JLabel(String.valueOf(camello.getIdCamello()), SwingConstants.RIGHT);
            lblNombre.setFont(new Font("Arial", Font.BOLD, 14));
            lblNombre.setBounds(10, 70 + i * 80, 90, 40);
            bgPanel.add(lblNombre);

            JLabel lbl = new JLabel("🐫", SwingConstants.CENTER);
            lbl.setFont(new Font("Dialog", Font.PLAIN, 34));
            int c = i % COLORS.length;
            lbl.setForeground(COLORS[c]);
            lbl.setBounds(105, 70 + i * 80, 40, 40);
            camelLabels.add(lbl);
            bgPanel.add(lbl);
        }

        buttonRun.addActionListener(e -> {
            resetCarrera();
            buttonRun.setEnabled(false);
            labelWinner.setText("");
            statusBar.setText("¡Carrera en marcha! [ID Carrera: " + idCarrera +
                    ", IP: " + ipGrupo.getHostAddress() +
                    ", Puerto: " + puerto + "]");
            new Thread(this).start();
        });

        bgPanel.repaint();
    }

    // maetodos carrera

    public boolean estaLlena() {
        if (camellos.size() < 4) {
            return false;
        } else return true;
    }

    public void agregarCamello(int idCamello) throws IOException {
        Camello camello = new Camello(idCamello);
        camellos.add(camello);
    }

    private void moverCamello(int pasos, int idCamello) throws IOException {
        int indice = 0;
        Camello cam = new Camello();
        for (int i = 0; i < camellos.size(); i++) {
            if (camellos.get(i).getIdCamello() == idCamello) {
                cam = camellos.get(i);
                indice = i;
            }
        }
        camelLabels.get(indice).setBounds(105 + cam.getPosicion(), 70 + indice * 80, 40, 40);
        if (camellos.get(indice).getPosicion() + pasos >= FINISH_LINE_X) {
            setCarreraTerminada(true);
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
            udp.enviar(new EventoCarrera(TipoEvento.SALIDA));

            // Recibir eventos
            while (!carreraTerminada) {
                EventoCarrera ev = udp.recibir();

                if (ev.getTipoEvento() == TipoEvento.PASO) {
                    int id = //id camello
                    int pasos = (int) (Math.random() * 3) + 1;
                    moverCamello(pasos, id);
                } else if (ev.getTipoEvento() == TipoEvento.META) {
                    udp.enviar(new EventoCarrera(TipoEvento.FIN));
                    carreraTerminada = true;
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