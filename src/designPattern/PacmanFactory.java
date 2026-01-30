package designPattern;

import model.Agent;
import model.Pacman;
import model.PositionAgent;

public class PacmanFactory implements AgentFactory {
    public PacmanFactory() {
    }

    public Agent createAgent(PositionAgent positionAgent) {
        return new Pacman(positionAgent);
    }
}
