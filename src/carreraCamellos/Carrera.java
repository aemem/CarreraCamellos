package carreraCamellos;

import mensajes.EventoCarrera;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.net.MulticastSocket;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import java.net.InetAddress;

import static mensajes.TipoEvento.*;

public class Carrera extends JFrame implements Runnable {
    // atributos de la interfaz
    private static final Color COLORS[] = {Color.GREEN, Color.RED, Color.YELLOW, Color.CYAN};
    private static final int FINISH_LINE_X = 600;
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
    private List<Integer> camellos;
    private final int idCamelloLocal;
    private boolean carreraIniciada = false;

    private InetAddress ipGrupo; // dirección multicast del grupo
    private int puerto;
    private UDPmulticast udp;


    // Constructor
    public Carrera(int idCarrera, InetAddress ipGrupo, int puerto, int idCamelloLocal) {
        this.idCarrera = idCarrera;
        this.ipGrupo = ipGrupo;
        this.puerto = puerto;
        this.idCamelloLocal = idCamelloLocal;
        camelLabels = new ArrayList<>();
        camellos = new ArrayList<>();

        setTitle("Carrera #" + idCarrera);
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        statusBar = new JLabel("Esperando...");
        statusBar.setBounds(10, 10, 780, 30);
        add(statusBar);

        buttonRun = new JButton("Paso");
        buttonRun.setBounds(340, 300, 120, 40);
        buttonRun.addActionListener(e -> {
            try {
                avanzarCamelloLocal();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        for (int i = 0; i < 4; i++) {
            JLabel lbl = new JLabel();
            lbl.setFont(new Font("Dialog", Font.PLAIN, 40));
            lbl.setBounds(50, 50 + i * 70, CAMEL_ICON_SIZE, CAMEL_ICON_SIZE);
            camelLabels.add(lbl);
            add(lbl);
        }

        JPanel panelFondo = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(220, 220, 220));
                for (int i = 0; i <= 4; i++) {
                    int y = 70 + i * 70;
                    g.drawLine(0, y, getWidth(), y);
                }
                g.setColor(Color.BLACK);
                g.fillRect(FINISH_LINE_X, 50, 5, 280);

                Graphics2D g2d = (Graphics2D) g;
                g2d.setFont(new Font("Arial", Font.BOLD, 20));
                g2d.setColor(Color.BLACK);
                AffineTransform old = g2d.getTransform();
                g2d.rotate(-Math.PI / 2);
                g2d.drawString("META", -200, FINISH_LINE_X + 20);
                g2d.setTransform(old);
            }
        };
        panelFondo.setBounds(0, 0, 800, 400);

        panelFondo.add(buttonRun);

        add(panelFondo);

        setVisible(true);

        try {
            udp = new UDPmulticast(ipGrupo, puerto);
            udp.socket = new MulticastSocket(puerto);
            udp.socket.joinGroup(ipGrupo);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // getters y setters
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

    public boolean estaLlena() {
        return camellos.size() >= 4;
    }

    public void agregarCamello(int idCamello) {
        if (camellos.size() >= numCamellos) return;
        camellos.add(idCamello);
        int idx = camellos.size() - 1;
        if (idx >= 0 && idx < camelLabels.size()) {
            JLabel lbl = camelLabels.get(idx);
            lbl.setForeground(COLORS[idx % COLORS.length]);
            ImageIcon icon = loadCamelIcon(CAMEL_ICON_SIZE, CAMEL_ICON_SIZE);
            if (icon != null) {
                lbl.setText("");
                lbl.setIcon(icon);
            }
        }
        repaint();
    }

    private ImageIcon loadCamelIcon(int w, int h) {
        URL url = getClass().getResource("resources/camel.png");
        if (url == null) {
            System.out.println("No se encontró camel.png");
            return null;
        }
        Image img = new ImageIcon(url).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    private void avanzarCamelloLocal() throws IOException {
        if (carreraTerminada) return;
        int pasos = (int) (Math.random() * 3) + 1;
        moverCamello(pasos, idCamelloLocal);

        try {
            EventoCarrera ev = new EventoCarrera(idCamelloLocal, PASO);
            ev.setPasos(pasos);
            udp.enviar(ev);
            JLabel lbl = camelLabels.get(camellos.indexOf(idCamelloLocal));

            if (lbl.getX() >= FINISH_LINE_X) {
                EventoCarrera meta = new EventoCarrera(idCamelloLocal, META);
                udp.enviar(meta);
                carreraTerminada = true;
                statusBar.setText("LLegaste a la meta");
                buttonRun.setEnabled(false);

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    void moverCamello(int pasos, int idCamello) throws IOException {
        int idx = camellos.indexOf(idCamello);
        if (idx < 0 || idx >= camelLabels.size()) return;
        JLabel lbl = camelLabels.get(idx);
        Point pos = lbl.getLocation();
        lbl.setLocation(pos.x + pasos * 10, pos.y);
        repaint();
    }

    public void eliminarCamello(int idCamello) {
        for (int i = 0; i < camellos.size(); i++) {
            if (camellos.get(i) == idCamello) {
                camellos.remove(i);
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

    public void marcarMeta(int idCamello) {
        SwingUtilities.invokeLater(() -> labelWinner.setText("Llegó a meta: Camello " + idCamello));
    }

    public void finalizarCarrera(int idGanador) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, "Camello " + idGanador + " ganó la carrera!");
            labelWinner.setText("Ganador: Camello " + idGanador);
            carreraTerminada = true;
            buttonRun.setEnabled(false);
        });
    }

    @Override
    public void run() {

        while (!carreraTerminada) {
            try {

                EventoCarrera ev = udp.recibir();

                switch (ev.getTipoEvento()) {
                    case PASO:
                        moverCamello(ev.getIdEmisor(), ev.getPasos());
                        break;
                    case META:
                        carreraTerminada = true;
                        moverCamello(ev.getIdEmisor(), FINISH_LINE_X);
                        statusBar.setText("Camello " + ev.getIdEmisor() + " llegó a la meta");
                        buttonRun.setEnabled(false);
                        break;
                    case CAIDA:
                        eliminarCamello(ev.getIdEmisor());
                        break;
                    default:

                }


            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
