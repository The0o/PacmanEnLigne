package designPattern;

import model.Agent;
import model.Fantome;
import model.PositionAgent;

public class FantomeFactory implements AgentFactory {
    public FantomeFactory() {
    }

    public Agent createAgent(PositionAgent positionAgent) {
        return new Fantome(positionAgent);
    }
}