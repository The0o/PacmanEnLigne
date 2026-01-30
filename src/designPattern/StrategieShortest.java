package designPattern;

import game.PacmanGame;
import model.Agent;
import model.PositionAgent;

public class StrategieShortest extends AbstractStrategieBFS {

    @Override
    public PositionAgent getTargetPosition(Agent agent, PacmanGame pacmanGame) {
        return pacmanGame.getPacmanPosition();
    }
    
}
