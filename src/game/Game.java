package game;

import designPattern.Observable;
import designPattern.Observateur;
import java.util.ArrayList;
import java.util.List;

public abstract class Game implements Runnable, Observable {
    public int turn;
    public int maxTurn;
    public boolean isRunning;
    public Thread thread;
    public long timeSleep = 200L;
    public List<Observateur> observateurs = new ArrayList<>();

    public Game(int maxTurn) {
        this.maxTurn = maxTurn;
    }

    public void launch() {
        if (this.thread != null && this.thread.isAlive()) return;
        this.isRunning = true;
        this.thread = new Thread(this);
        this.thread.start();
    }

    public void init() {
        this.turn = 0;
        this.isRunning = true;
        this.initializeGame();
        this.notifierObservateurs();
    }

    public abstract void initializeGame();

    public void step() {
        ++this.turn;
        if (this.gameContinue() && this.maxTurn > this.turn) {
            this.takeTurn();
        } else {
            this.isRunning = false;
            this.gameOver();
        }

        this.notifierObservateurs();
    }

    public void pause() {
        this.isRunning = false;
    }

    public void run() {
        for(; this.isRunning; this.step()) {
            try {
                Thread.sleep(this.timeSleep);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public abstract void gameOver();

    public abstract boolean gameContinue();

    public abstract void takeTurn();

    public void enregistrerObservateur(Observateur observateur) {
        this.observateurs.add(observateur);
    }

    public void supprimerObservateur(Observateur observateur) {
        this.observateurs.remove(observateur);
    }

    public void notifierObservateurs() {
        System.out.println("notifier obervateur" + this.observateurs.toString());

        for(int i = 0; i < this.observateurs.size(); ++i) {
            Observateur observateur = (Observateur)this.observateurs.get(i);
            observateur.actualiser(this);
        }

    }
}