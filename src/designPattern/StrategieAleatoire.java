package designPattern;

import game.PacmanGame;
import java.util.Random;
import model.Agent;
import model.AgentAction;

public class StrategieAleatoire implements StrategieAgent {
    public StrategieAleatoire() {
    }

    public AgentAction getAction(Agent agent, PacmanGame game) {
        AgentAction action = null;
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
