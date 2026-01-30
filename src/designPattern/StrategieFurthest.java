package designPattern;

import game.PacmanGame;
import model.Agent;
import model.Maze;
import model.PositionAgent;

public class StrategieFurthest extends AbstractStrategieBFS {

    @Override
    public PositionAgent getTargetPosition(Agent agent, PacmanGame pacmanGame) {
        //utilise quand un pacman peut manger un fantome, ceux-ci vont donc fuir
        PositionAgent pacman = pacmanGame.getPacmanPosition();
        Maze maze = pacmanGame.maze;
        PositionAgent bestTarget = new PositionAgent(-1, -1, 0);
        double maxDistance = -1;

        for (int x = 0; x < maze.getSizeX(); x++) {
            for (int y = 0; y < maze.getSizeY(); y++) {
                if (!maze.isWall(x, y)) {
                    double dist = Math.pow(x-pacman.getX(), 2) + Math.pow(y - pacman.getY(), 2);
                    if (dist > maxDistance) {
                        maxDistance = dist;
                        bestTarget = new PositionAgent(x, y, 0);
                    }
                }
            }   
        }
        return bestTarget;
    }
    
}
