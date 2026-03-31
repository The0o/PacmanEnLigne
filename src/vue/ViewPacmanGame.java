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
import model.Fantome;
import model.GameStateModel;
import model.Maze;
import model.Pacman;
import model.PositionAgent;

public class ViewPacmanGame implements Observateur {

    public PanelPacmanGame panelPacman;
    public JFrame jFrame;
    public boolean partieTerminee = false;
    private String sessionCookie;
    private String usernameConnecte;

    public ViewPacmanGame(Maze maze) {
        this(maze, null, null);
    }

    public ViewPacmanGame(Maze maze, String sessionCookie, String usernameConnecte) {
        this.sessionCookie = sessionCookie;
        this.usernameConnecte = usernameConnecte;
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
    
    public void actualiserClient(GameStateModel gameState) {
    	if (gameState.isRunning()) {
        	this.panelPacman.setMaze(gameState.getMaze());
        	this.panelPacman.setPacmans_pos(gameState.getPositionsPacmans());
        	this.panelPacman.setPacmansUsernames(gameState.getPacmansUsernames()); // NOUVEAU
        	this.panelPacman.setGhostsScarred(gameState.getEffraye());
        	this.panelPacman.setGhosts_pos(gameState.getPositionsFantomes());
    	}
    	else {
    		finPartie("FIN DE PARTIE", Color.gray);
    	}
    	this.panelPacman.repaint();
    }
    
    @Override
    public void actualiser(Game game) {
        if (partieTerminee) return;
        
        PacmanGame pacmanGame = (PacmanGame) game;
        boolean pacmanVivant = false;
        this.panelPacman.setMaze(pacmanGame.getMaze());
        ArrayList<PositionAgent> pacman = new ArrayList<>();
        ArrayList<String> pseudosLocaux = new ArrayList<>(); // NOUVEAU
        ArrayList<PositionAgent> fantome = new ArrayList<>();
        
        int compteurJoueur = 1;
        for (Agent agent : pacmanGame.listeAgent) {
            if (agent instanceof Pacman) {
                if (agent.getPosition().getX() != -1) {
                    pacmanVivant = true;
                    pacman.add(agent.getPosition());
                    pseudosLocaux.add("J" + compteurJoueur++); // Pseudo par defaut hors-ligne
                }
            }
            else {
                fantome.add(agent.getPosition());
            }
        }

        panelPacman.setPacmans_pos(pacman);
        panelPacman.setPacmansUsernames(pseudosLocaux); // NOUVEAU
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

    public void finPartie(String message, Color couleur) {
        this.partieTerminee = true;
        panelPacman.afficherMessageFin(message, couleur);
        Timer timer = new Timer(5000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (Window window: Window.getWindows()) {
                    window.dispose();
                }
                new GameLauncherEnLigne(sessionCookie, usernameConnecte);
            }
        });
        timer.setRepeats(false);
        timer.start();
    }
}
