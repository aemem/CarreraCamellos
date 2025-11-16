package carreraCamellos;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.net.InetAddress; // Import necesario para InetAddress

public class Carrera extends JFrame implements Runnable {
    private boolean carreraTerminada;
    private static final int numCamellos = 4;
    private static final Color COLORS[] = {Color.GREEN, Color.RED, Color.YELLOW, Color.CYAN};
    public static final int FINISH_LINE_X = 600;
    private static int contadorCarreras = 1;
    private int idCarrera;
    private List<Camello> camellos;
    private ArrayList<JLabel> camelLabels;
    private JButton buttonRun;
    private JPanel bgPanel;
    private JLabel labelWinner;
    private JLabel statusBar;

    // NUEVOS ATRIBUTOS PARA MULTICAST
    private InetAddress ipGrupo; // dirección multicast del grupo
    private int puerto;          // puerto multicast que va a usar la carrera

    // Constructor que recibe la Lista<Camello>, dirección multicast y puerto
    public Carrera(List<Camello> camellos, InetAddress ipGrupo, int puerto) {
        this.camellos = camellos;
        this.ipGrupo = ipGrupo;
        this.puerto = puerto;
        this.idCarrera = contadorCarreras++;
        camelLabels = new ArrayList<>();
        crearInterfaceUI();

        for (int i = 0; i < camellos.size(); i++) {
            Camello camello = camellos.get(i);

            JLabel lblNombre = new JLabel(camello.getNombre(), SwingConstants.RIGHT);
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

    @Override
    public void run() {
        carreraTerminada = false;

        while (!carreraTerminada) {
            //llamar al metodo recibir pasos.
                }
            }
            try { Thread.sleep(70); }
            catch (InterruptedException ex) { Thread.currentThread().interrupt(); }
        }
        buttonRun.setEnabled(true);
        buttonRun.setText("Nueva carrera");
        statusBar.setText("Carrera finalizada [ID: " + idCarrera +
                ", IP: " + ipGrupo.getHostAddress() +
                ", Puerto: " + puerto + "] Pulsa para nueva partida.");
    }

    private void recibirPasos(int pasos){
         //reciben los pasos y mueve el camello ;

    }

    private void mostrarGanador(String nombre, int idx) {
        labelWinner.setText("¡El ganador es: " + nombre + "!");
        labelWinner.setForeground(new Color(90, 30, 180));
        labelWinner.setFont(new Font("Arial", Font.BOLD, 26));
        camelLabels.get(idx).setFont(new Font("Dialog", Font.BOLD, 42));
        statusBar.setText("Ganador: " + nombre +
                " [ID Carrera: " + idCarrera +
                ", IP: " + ipGrupo.getHostAddress() +
                ", Puerto: " + puerto + "]");
    }

    private void resetCarrera() {
        for (int i = 0; i < camellos.size(); i++) {
            camellos.get(i).reset();
            camelLabels.get(i).setBounds(105, 70 + i * 80, 40, 40);
            camelLabels.get(i).setFont(new Font("Dialog", Font.PLAIN, 34));
        }
        labelWinner.setText("");
        labelWinner.setForeground(Color.BLACK);
        buttonRun.setText("Run");
    }

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
    }

    public static void main(String[] args) {
        try {
            List<Camello> miListaCamellos = new ArrayList<>();
            for (int i = 0; i < numCamellos; i++) {
                miListaCamellos.add(new Camello("Camello " + (i + 1)));
            }
            InetAddress ipGrupo = InetAddress.getByName("224.0.0.1"); // Ejemplo dirección multicast válida
            int puerto = 5000; // Ejemplo puerto

            EventQueue.invokeLater(() -> {
                Carrera frame = new Carrera(miListaCamellos, ipGrupo, puerto);
                frame.pack();
                frame.setVisible(true);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
