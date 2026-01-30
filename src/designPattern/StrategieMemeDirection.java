package designPattern;

import game.PacmanGame;
import java.util.Random;
import model.Agent;
import model.AgentAction;

public class StrategieMemeDirection implements StrategieAgent {
    public StrategieMemeDirection() {
    }

    public AgentAction getAction(Agent agent, PacmanGame game) {
        int dir = agent.getPosition().getDir();
        AgentAction action = new AgentAction(dir);
        if (game.isLegalMove(agent, action)) {
            return action;
        } else {
            Random random = new Random();
            boolean movePossible = false;

            while(!movePossible) {
                int randomDirection = random.nextInt(4);
                action = new AgentAction(randomDirection);
                if (game.isLegalMove(agent, action)) {
                    movePossible = true;
                }
            }

            return action;
        }
    }
}