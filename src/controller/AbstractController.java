package controller;

import game.Game;

public abstract class AbstractController {
    public Game game;

    public AbstractController() {
    }

    public abstract void restart();

    public abstract void step();

    public abstract void play();

    public abstract void pause();

    public abstract void setSpeed(double speed);

    public abstract void keyInput(int input);
}
