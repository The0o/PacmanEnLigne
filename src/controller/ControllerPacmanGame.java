package controller;

import designPattern.EtatJeu;
import designPattern.EtatLance;
import designPattern.EtatPause;
import designPattern.EtatStoppe;
import designPattern.StrategieInteractif;
import game.PacmanGame;
import java.util.Iterator;

import javax.swing.JFrame;

import clientMain.GameClient;
import model.Agent;
import model.Pacman;
import vue.ViewCommand;
import vue.ViewPacmanGame;

public class ControllerPacmanGame extends AbstractController {
    
    public EtatJeu etatCourant;
    public ViewCommand viewCommand;
    public GameClient gameClient;

    public ControllerPacmanGame(String layout, double difficultePourcentage, GameClient gameClient) throws Exception {
    	this.gameClient = gameClient;
        this.game = new PacmanGame(1000, layout, difficultePourcentage);
        ViewPacmanGame viewGame = new ViewPacmanGame(((PacmanGame)this.game).getMaze());
        this.game.enregistrerObservateur(viewGame);

        this.viewCommand = new ViewCommand(this);
        this.game.enregistrerObservateur(viewCommand);

        JFrame gameFrame = viewGame.getJFrame();
        JFrame commandFrame = viewCommand.getJFrame();
        int xPos = gameFrame.getX() + gameFrame.getWidth();
        int yPos = gameFrame.getY();
        commandFrame.setLocation(xPos, yPos);

        setEtat(new EtatStoppe());

        this.game.init();
    }

    public void setEtat(EtatJeu nouvelEtat) {
        this.etatCourant = nouvelEtat;
        if (viewCommand != null) {
            this.etatCourant.updateBouton(viewCommand);
        }
    }

    public void restart() {
        this.game.init();
        setEtat(new EtatStoppe());
    }

    public void step() {
        this.game.step();
        setEtat(new EtatPause());
    }

    public void play() {
        this.game.launch();
        setEtat(new EtatLance());
    }

    public void pause() {
        this.game.pause();
        setEtat(new EtatPause());
    }

    public void setSpeed(double speed) {
        this.game.timeSleep = (long) (1000L / speed);
    }

    public void keyInput(int direction) {
        PacmanGame pacmanGame = (PacmanGame)this.game;
        Iterator<Agent> iterator = pacmanGame.listeAgent.iterator();

        while(iterator.hasNext()) {
            Agent agent = (Agent)iterator.next();
            if (agent instanceof Pacman && agent.getStrategie() instanceof StrategieInteractif) {
                StrategieInteractif strategie = (StrategieInteractif)agent.getStrategie();
                strategie.setLastActionDirection(direction);
            }
        }

    }
}
