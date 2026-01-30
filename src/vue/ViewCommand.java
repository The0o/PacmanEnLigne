package vue;

import controller.AbstractController;
import designPattern.Observateur;
import game.Game;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ViewCommand implements Observateur, KeyListener {
    JLabel nbTours = new JLabel("Turn : ", 0);
    AbstractController controller;
    public JFrame jFrame;
    private JButton restartButton;
    private JButton pauseButton;
    private JButton runButton;
    private JButton stepButton;

    public ViewCommand(final AbstractController controller) {
        this.controller = controller;
        jFrame = new JFrame();
        jFrame.setTitle("Commands");
        jFrame.setSize(new Dimension(700, 700));
        Dimension windowSize = jFrame.getSize();
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Point centerPoint = ge.getCenterPoint();
        int dx = centerPoint.x - windowSize.width / 2;
        int dy = centerPoint.y - windowSize.height / 2;
        jFrame.setLocation(dx, dy);
        JPanel mainPanel = new JPanel(new GridLayout(2, 1));
        JPanel topPanel = new JPanel(new GridLayout(1, 4));
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2));
        jFrame.addKeyListener(this);
        jFrame.setFocusable(true);

        //-------------4 BOUTONS COMMANDES-------------
        ImageIcon restartIcon = new ImageIcon("icons/icon_restart.png");
        restartButton = new JButton(restartIcon);
        restartButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.restart();
            }
        });
        restartButton.setFocusable(false);
        ImageIcon pauseIcon = new ImageIcon("icons/icon_pause.png");
        pauseButton = new JButton(pauseIcon);
        pauseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.pause();
            }
        });
        pauseButton.setFocusable(false);
        ImageIcon runIcon = new ImageIcon("icons/icon_run.png");
        runButton = new JButton(runIcon);
        runButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.play();
            }
        });
        runButton.setFocusable(false);
        ImageIcon stepIcon = new ImageIcon("icons/icon_step.png");
        stepButton = new JButton(stepIcon);
        stepButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.step();
            }
        });
        stepButton.setFocusable(false);
        topPanel.add(restartButton);
        topPanel.add(pauseButton);
        topPanel.add(runButton);
        topPanel.add(stepButton);

        //-------------SLIDER VITESSE-------------
        JSlider slider = new JSlider(1, 10);
        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                JSlider jFrame = (JSlider)e.getSource();
                if (!jFrame.getValueIsAdjusting()) {
                    controller.setSpeed((double)jFrame.getValue());
                }

            }
        });
        slider.setMajorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setFocusable(false);
        bottomPanel.add(slider);
        bottomPanel.add(this.nbTours);
        mainPanel.add(topPanel);
        mainPanel.add(bottomPanel);
        jFrame.add(mainPanel);
        jFrame.setVisible(true);
    }

    public void distribuerBoutons(boolean restart, boolean pause, boolean run, boolean step) {
        restartButton.setEnabled(restart);
        pauseButton.setEnabled(pause);
        runButton.setEnabled(run);
        stepButton.setEnabled(step);
    }

    public JFrame getJFrame() {
        return jFrame;
    }

    public void actualiser(Game game) {
        this.nbTours.setText("Tour : " + game.turn);
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == 38 || e.getKeyCode() == 40 || e.getKeyCode() == 37 || e.getKeyCode() == 39) {
            //Au lieu de devoir clicker sur commencer, si l'utilisateur appuie sur une touche pour
            //bouger, on commence le jeu
            this.controller.play();
        }
        if (e.getKeyCode() == 38) {
            this.controller.keyInput(0);
        } else if (e.getKeyCode() == 40) {
            this.controller.keyInput(1);
        } else if (e.getKeyCode() == 37) {
            this.controller.keyInput(3);
        } else if (e.getKeyCode() == 39) {
            this.controller.keyInput(2);
        }

    }

    public void keyReleased(KeyEvent e) {
    }
}