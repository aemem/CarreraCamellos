package carreraCamellos;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class Carrera extends JFrame implements Runnable {
    private boolean carreraTerminada = false;
    private static final int numCamellos = 4; // número de camellos
    private static final Color COLORS[] = {Color.GREEN, Color.RED, Color.YELLOW, Color.CYAN};
    private static final int FINISH_LINE_X = 600;
    private ArrayList<Camello> camellos;
    private ArrayList<JLabel> camelLabels;
    private JButton buttonRun;
    private JPanel bgPanel;
    private JLabel labelWinner;

    public Carrera() {
        camellos = new ArrayList<>();
        camelLabels = new ArrayList<>();
        createUIComponents();
        // Inicializa camellos y gráfico de cada uno
        for (int i = 0; i < numCamellos; i++) {
            JLabel lbl = new JLabel("🐫", SwingConstants.CENTER);
            int c = (int)( Math.random() * 3); // cada vez que te crea un carril te crea un color
            lbl.setForeground(COLORS[c]);
            lbl.setBounds(0, 70 + i * 80, 40, 40);
            bgPanel.add(lbl);
            camelLabels.add(lbl);
        }

        buttonRun.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                carreraTerminada = false;

                // Lanzamos el hilo de carrera
                new Thread(Carrera.this).start(); //Empieza la carrera
            }
        });
    }

    @Override
    public void run() {
        while (!carreraTerminada) {
            for (int i = 0; i < camellos.size(); i++) {
                Camello c = camellos.get(i);
                if (!c.isHaLlegado()) { //depende del atributo que se declare en la clase camello
                    c.avanzar(); // metodo que deberia integrar la clase camello
                    camelLabels.get(i).setBounds(c.getPosicion(), 70 + i * 80, 40, 40); // posicion de la clase camello
                    if (c.isHaLlegado()) { // atributo de la clase camello
                        carreraTerminada = true;
                        labelWinner.setText("El Ganador es: " + c.getNombre()); // de la clase camello
                        break;
                    }
                }
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void createUIComponents() {
        bgPanel = new JPanel(null);
        setContentPane(bgPanel);
        setTitle("Carrera de Camellos");
        setSize(800, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel finishLine = new JPanel();
        finishLine.setBackground(Color.BLACK);
        finishLine.setBounds(FINISH_LINE_X, 70, 5, 330);
        bgPanel.add(finishLine);

        buttonRun = new JButton("Run");
        buttonRun.setBounds(350, 400, 80, 30);
        bgPanel.add(buttonRun);

        labelWinner = new JLabel("");
        labelWinner.setFont(new Font("Arial", Font.BOLD, 20));
        labelWinner.setForeground(Color.BLACK);
        labelWinner.setHorizontalAlignment(JLabel.CENTER);
        labelWinner.setBounds(0, 20, 800, 30);
        bgPanel.add(labelWinner);
    }

    public static void main(String args[]) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                Carrera frame = new Carrera();
                frame.setVisible(true);
            }
        });
    }
}
