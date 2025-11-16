package carreraCamellos;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Clase Carrera: representa una carrera de camellos.
 * Cada objeto Carrera es un hilo independiente (implementa Runnable).
 * Tiene un ID único y una lista de camellos (clientes).
 */
public class Carrera extends JFrame implements Runnable {
    // Control de terminación de carrera
    private boolean carreraTerminada = false;

    // Número total de camellos en la carrera (puede cambiar en nuevas carreras)
    private static final int numCamellos = 4;

    // Colores usados para camellos
    private static final Color COLORS[] = {Color.GREEN, Color.RED, Color.YELLOW, Color.CYAN};

    // Posición X de la línea de meta
    private static final int FINISH_LINE_X = 600;

    // ID de la carrera, suma +1 por cada carrera creada
    private static int contadorCarreras = 1;     // variable estática para autoincremento
    private int idCarrera;                       // id único de esta carrera

    // Lista de camellos que compiten en esta carrera (es una List, no ArrayList)
    private List<Camello> camellos;
    private ArrayList<JLabel> camelLabels;

    // Elementos visuales (Swing)
    private JButton buttonRun;
    private JPanel bgPanel;
    private JLabel labelWinner;
    private JLabel statusBar;

    /**
     * Constructor de Carrera: recibe la lista de camellos para esta carrera.
     * Asigna un id único.
     */
    public Carrera(List<Camello> camellos) {
        this.camellos = camellos;               // Recibe la lista por parámetro
        this.idCarrera = contadorCarreras++;    // Asigna id y lo incrementa estáticamente
        camelLabels = new ArrayList<>();
        crearInterfaceUI();

        // Inicializa las etiquetas visuales de cada camello
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
            carreraTerminada = false;
            buttonRun.setEnabled(false);
            statusBar.setText("¡Carrera en marcha! [ID Carrera: " + idCarrera + "]");
            labelWinner.setText("");
            new Thread(Carrera.this).start();
        });

        bgPanel.repaint();
    }

    /**
     * run() - Lógica principal de la carrera (hilo independiente).
     * Avanza los camellos en pasos aleatorios de 1 a 3 hasta que uno llegue a la meta.
     */
    @Override
    public void run() {
        while (!carreraTerminada) {
            for (int i = 0; i < camellos.size(); i++) {
                Camello c = camellos.get(i);
                if (!c.isHaLlegado()) {
                    int pasos = generarPasoAleatorio(); // Nuevo método para avanzar
                    c.avanzar(pasos);                  // Debe recibir pasos en la clase Camello
                    camelLabels.get(i).setBounds(105 + c.getPosicion(), 70 + i * 80, 40, 40);
                    if (c.isHaLlegado()) {
                        carreraTerminada = true;
                        mostrarGanador(c.getNombre(), i);
                        break;
                    }
                }
            }
            try {
                Thread.sleep(70);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        buttonRun.setEnabled(true);
        buttonRun.setText("Nueva carrera");
        statusBar.setText("Carrera finalizada [ID: " + idCarrera + "] Pulsa para nueva partida.");
    }

    /**
     * Método para obtener pasos aleatorios (1 a 3)
     */
    private int generarPasoAleatorio() {
        return (int) (Math.random() * 3) + 1;
    }

    private void mostrarGanador(String nombre, int idx) {
        labelWinner.setText("¡El ganador es: " + nombre + "!");
        labelWinner.setForeground(new Color(90, 30, 180));
        labelWinner.setFont(new Font("Arial", Font.BOLD, 26));
        camelLabels.get(idx).setFont(new Font("Dialog", Font.BOLD, 42));
        statusBar.setText("Ganador: " + nombre + " [ID Carrera: " + idCarrera + "]");
    }

    /**
     * Reinicia la carrera a la posición inicial.
     */
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
        // Ejemplo de uso: crea lista y lanza carrera
        List<Camello> miListaCamellos = new ArrayList<>();
        for (int i = 0; i < numCamellos; i++) {
            miListaCamellos.add(new Camello("Camello " + (i + 1)));
        }
        EventQueue.invokeLater(() -> {
            Carrera frame = new Carrera(miListaCamellos);
            frame.pack();
            frame.setVisible(true);
        });
    }
}
