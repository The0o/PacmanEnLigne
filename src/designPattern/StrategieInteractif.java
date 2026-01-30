package designPattern;

import game.PacmanGame;
import model.Agent;
import model.AgentAction;

public class StrategieInteractif implements StrategieAgent {
    private int lastActionDirection = 4;

    public StrategieInteractif() {
    }

    public void setLastActionDirection(int direction) {
        this.lastActionDirection = direction;
    }

    public AgentAction getAction(Agent agent, PacmanGame game) {
        //utilise pour le pacman
        AgentAction action = new AgentAction(this.lastActionDirection);
        if (game.isLegalMove(agent, action)) {
            return action;
        } else {
            AgentAction actionMemeDirection = new AgentAction(agent.getPosition().getDir());
            return game.isLegalMove(agent, actionMemeDirection) ? actionMemeDirection : new AgentAction(4);
        }
    }
}