package vue;

import javax.swing.*;

import autre.PanelPacmanGame;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import designPattern.Observateur;
import game.Game;
import game.PacmanGame;
import model.Agent;
import model.Maze;
import model.Pacman;
import model.PositionAgent;

public class ViewPacmanGame implements Observateur {

    public PanelPacmanGame panelPacman;
    public JFrame jFrame;
    public boolean partieTerminee = false;

    public ViewPacmanGame(Maze maze) {
        jFrame = new JFrame();
        jFrame.setTitle("Pacmann");
        Dimension windowSize = jFrame.getSize();
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Point centerPoint = ge.getCenterPoint();
        int dx = centerPoint.x - windowSize.width / 2 ;
        int dy = centerPoint.y - windowSize.height / 2;
        jFrame.setLocation(dx, dy);
        this.panelPacman = new PanelPacmanGame(maze);
        int tailleCase = 20;
        this.panelPacman.setPreferredSize(new Dimension(maze.getSizeX() * tailleCase, maze.getSizeY() * tailleCase));
        jFrame.add(panelPacman);
        jFrame.pack();
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);
    }

    public JFrame getJFrame() {
        return jFrame;
    }

    @Override
    public void actualiser(Game game) {
        if (partieTerminee) return;
        //evite un bug qui lançait/fermait tres rapidement des dizaines de fenetre au moment de revenir
        //a l'ecran principale
        
        PacmanGame pacmanGame = (PacmanGame) game;
        boolean pacmanVivant = false;
        this.panelPacman.setMaze(pacmanGame.getMaze());
        ArrayList<PositionAgent> pacman = new ArrayList<>();
        ArrayList<PositionAgent> fantome = new ArrayList<>();
        for (Agent agent : pacmanGame.listeAgent) {
            if (agent instanceof Pacman) {
                if (agent.getPosition().getX() != -1) {
                    pacmanVivant = true;
                    pacman.add(agent.getPosition());
                }
            }
            else {
                fantome.add(agent.getPosition());
            }
        }

        panelPacman.setPacmans_pos(pacman);
        panelPacman.setGhosts_pos(fantome);

        if (pacmanGame.getCapsuleTimer() > 0) {
            panelPacman.setGhostsScarred(true);
        }
        else {
            panelPacman.setGhostsScarred(false);
        }

        if (!pacmanVivant) {
            finPartie("GAME OVER...", Color.RED);
        }
        else if (!pacmanGame.gameContinue() && pacmanVivant) {
            finPartie("VICTOIRE !", Color.GREEN);
        }

        panelPacman.repaint();
    }

    /*
    L'utilisateur a perdu ou gagne, on affiche le message avant
    de le faire revenir sur l'ecran d'accueil
    */
    public void finPartie(String message, Color couleur) {
        this.partieTerminee = true;
        panelPacman.afficherMessageFin(message, couleur);
        Timer timer = new Timer(5000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (Window window: Window.getWindows()) {
                    window.dispose();
                }
                new GameLauncher();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }
    
}
