package designPattern;

import game.PacmanGame;
import model.Agent;
import model.AgentAction;

public interface StrategieAgent {
    AgentAction getAction(Agent agent, PacmanGame pacmanGame);
}
