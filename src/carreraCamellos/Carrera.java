package carreraCamellos;

import mensajes.EventoCarrera;
import mensajes.TipoEvento;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.MulticastSocket;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import java.net.InetAddress;

public class Carrera extends JFrame implements Runnable {
    // atributos de la interfaz
    private static final int START_LINE_X = 100;
    private static final int FINISH_LINE_X = 600;
    private static final int CAMEL_ICON_SIZE = 40;
    private final List<JLabel> camelLabels = new ArrayList<>();
    private final JButton buttonRun = new JButton("Paso");
    private final JLabel statusBar = new JLabel("Esperando SALIDA...", SwingConstants.LEFT);
    private ArrayList<JLabel> idLabels = new ArrayList<>();

    // atributos de la carrera
    private boolean carreraTerminada = false;
    private static final int NUM_CAMELLOS = 4;
    private final int idCarrera;
    private final List<Integer> camellos = new ArrayList<>();
    private final int idCamelloLocal;
    private final InetAddress ipGrupo;
    private final int puerto;
    private UDPmulticast udp;

    // Constructor
    public Carrera(int idCarrera, InetAddress ipGrupo, int puerto, int idCamelloLocal, MulticastSocket msSocket) {
        this.idCarrera = idCarrera;
        this.ipGrupo = ipGrupo;
        this.puerto = puerto;
        this.idCamelloLocal = idCamelloLocal;

        // creacion de la interfaz
        setTitle("Carrera #" + idCarrera);
        setSize(800, 420);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setBounds(0, 0, 800, 420);
        add(layeredPane);

        JPanel panelFondo = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(230, 230, 230));
                for (int i = 0; i <= NUM_CAMELLOS; i++) {
                    int y = 70 + i * 70;
                    g.drawLine(0, y, getWidth(), y);
                }
                g.setColor(Color.BLACK);
                g.fillRect(FINISH_LINE_X, 50, 5, 280);
                g.setFont(new Font("Arial", Font.BOLD, 18));
                g.drawString("META", FINISH_LINE_X + 8, 80);
            }
        };
        panelFondo.setBounds(0, 0, 800, 420);
        layeredPane.add(panelFondo, JLayeredPane.DEFAULT_LAYER);

        statusBar.setBounds(10, 10, 760, 30);
        layeredPane.add(statusBar, JLayeredPane.PALETTE_LAYER);

        // boton
        buttonRun.setBounds(330, 340, 120, 40);
        buttonRun.setEnabled(false);
        // el boton genera los pasos y envia PASO o META
        buttonRun.addActionListener(e -> {
            if (carreraTerminada) return;
            try {

                int pasos = (int)(Math.random() * 3) + 1;
                EventoCarrera ev = new EventoCarrera(idCamelloLocal, TipoEvento.PASO);
                ev.setPasos(pasos);
                udp.enviar(ev);
                System.out.println("El camello " + idCamelloLocal + "ha avanzado " + pasos + " pasos");
                int idx = camellos.indexOf(idCamelloLocal);
                JLabel lbl = camelLabels.get(idx);
                int posX = lbl.getX();
                int nuevaX = Math.min(posX + pasos * 10, FINISH_LINE_X);
                if (nuevaX == FINISH_LINE_X) {
                    EventoCarrera meta = new EventoCarrera(idCamelloLocal, TipoEvento.META);
                    meta.setPasos(pasos);
                    udp.enviar(meta);
                    carreraTerminada = true;
                    statusBar.setText("Has llegado a la meta!");
                    buttonRun.setEnabled(false);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
//
        });
        layeredPane.add(buttonRun, JLayeredPane.PALETTE_LAYER);

        // crear las labels para las imagenes de los camellos
        for (int i = 0; i < NUM_CAMELLOS; i++) {
            JLabel lbl = new JLabel();
            lbl.setBounds(START_LINE_X, 70 + i * 70, CAMEL_ICON_SIZE, CAMEL_ICON_SIZE);
            camelLabels.add(lbl);
            layeredPane.add(lbl, JLayeredPane.PALETTE_LAYER);

            JLabel lblID = new JLabel("ID?");
            lblID.setBounds(10, 70 + i * 70, 50, 20);
            idLabels.add(lblID);
            layeredPane.add(lblID, JLayeredPane.MODAL_LAYER);
        }

        // crear los iconos de los camellos
        actualizarEtiquetasCamellos();

        // Inicializar el multicast
        try {
            udp = new UDPmulticast(ipGrupo, puerto);
            udp.socket = msSocket;
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        setVisible(true);
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

    public List<Integer> getCamellos() {
        return camellos;
    }

    public void setCarreraTerminada(boolean carreraTerminada) {
        this.carreraTerminada = carreraTerminada;
    }

    // crear los iconos de los camellos
    public void actualizarEtiquetasCamellos() {
        ImageIcon icon = cargarImgCamello();

        for (int i = 0; i < camelLabels.size(); i++) {
            JLabel lbl = camelLabels.get(i);

            if (i < camellos.size()) {
                if (icon != null) {
                    lbl.setIcon(icon);
                    lbl.setText("");
                } else {
                    lbl.setIcon(null);
                    lbl.setText("\uD83D\uDC2B");
                }
                lbl.setVisible(true);
                lbl.setLocation(START_LINE_X, 70 + i * 70);
            } else {
                lbl.setIcon(null);
                lbl.setText("");
                lbl.setVisible(false);
            }
        }
        SwingUtilities.invokeLater(this::repaint);
    }

    // cargar el png del camello
    private ImageIcon cargarImgCamello() {
        URL url = getClass().getResource("/carreraCamellos/resources/camel.png");
        Image img = new ImageIcon(url).getImage()
                .getScaledInstance(CAMEL_ICON_SIZE, CAMEL_ICON_SIZE, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    // mover un camello en la interfaz
    public void moverCamello(int pasos, int idCamello) {

        int idx = camellos.indexOf(idCamello);
        JLabel lbl = camelLabels.get(idx);
        Point p = lbl.getLocation();
        int nuevaX = Math.min(p.x + pasos * 10, FINISH_LINE_X);
        lbl.setLocation(nuevaX, p.y);
        repaint();
    }

    // eliminar un camello de la carrera
    public void eliminarCamello(int idCamello) {
        int idx = camellos.indexOf(idCamello);
        if (idx >= 0) {
            camellos.remove(idx);
            repaint();
        }
    }

    @Override
    public void run() {
        try {
            while (!carreraTerminada) {
                // escuchar en el multicast y actualizar la interfaz segun los mensajes recibidos
                EventoCarrera ev = udp.recibir();
                switch (ev.getTipoEvento()) {
                    case SALIDA:
                        SwingUtilities.invokeLater(() -> {
                            statusBar.setText("Carrera iniciada!");
                            buttonRun.setEnabled(true);

                            camellos.clear();
                            camellos.addAll(ev.getListaCamellos()); // IDs ordenados como envía el servidor

                            for (int i = 0; i < ev.getListaCamellos().size(); i++) {
                                int id = ev.getListaCamellos().get(i);
                                idLabels.get(i).setText("ID " + id);
                            }
                            actualizarEtiquetasCamellos();
                        });
                        break;
                    case PASO:
                        SwingUtilities.invokeLater(() -> moverCamello(
                                ev.getPasos(),
                                ev.getIdEmisor()
                        ));
                        break;
                    case META:
                        SwingUtilities.invokeLater(() -> moverCamello(
                                ev.getPasos(),
                                ev.getIdEmisor()
                        ));
                            statusBar.setText("Camello " + ev.getIdEmisor() + " llegó a la meta");
                            carreraTerminada = true;
                            buttonRun.setEnabled(false);

                        break;
                    case CAIDA:
                        SwingUtilities.invokeLater(() -> eliminarCamello(ev.getIdEmisor()));
                        break;
                    case FIN:
                        SwingUtilities.invokeLater(() -> {
                            statusBar.setText("Carrera finalizada");
                            carreraTerminada = true;
                            buttonRun.setEnabled(false);
                        });
                        break;
                    default:
                        break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (udp != null && udp.socket != null) {
                try {
                    udp.socket.leaveGroup(ipGrupo);
                    udp.socket.close();
                } catch (IOException ignored) {
                }
            }
        }
    }
}
