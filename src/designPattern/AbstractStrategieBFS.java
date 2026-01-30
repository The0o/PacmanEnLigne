package designPattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import game.PacmanGame;
import model.Agent;
import model.AgentAction;
import model.Maze;
import model.PositionAgent;

public abstract class AbstractStrategieBFS implements StrategieAgent {

    public abstract PositionAgent getTargetPosition(Agent agent, PacmanGame pacmanGame);

    @Override
    public AgentAction getAction(Agent agent, PacmanGame pacmanGame) {
        //fonction pour trouver le chemin le plus rapide, mais pas avoir le chemin
        PositionAgent target = getTargetPosition(agent, pacmanGame);
        Queue<PositionAgent> queue = new LinkedList<PositionAgent>();
        queue.add(agent.getPosition());
        HashMap<PositionAgent, PositionAgent> predecessorMap = new HashMap<>();
        Maze m = pacmanGame.maze;
        ArrayList<PositionAgent> listVisited = new ArrayList<PositionAgent>();
        listVisited.add(agent.getPosition());
        while(!queue.isEmpty()) {
            PositionAgent node = (PositionAgent) queue.remove();
            if (node.equals(target)) {
                return reconstruireChemin(predecessorMap, target, agent.getPosition());
            }
            ArrayList<PositionAgent> childs = getUnvisitedChildNode(node, m);

            for(PositionAgent child : childs) {
                if (!listVisited.contains(child)) {
                    predecessorMap.put(child, node);
                    listVisited.add(child);
                    queue.add(child);
                }
            }
        }
        return new AgentAction(AgentAction.STOP);
    }

    private AgentAction reconstruireChemin(HashMap<PositionAgent,PositionAgent> predecessorMap, PositionAgent target, PositionAgent start) {
        //une fois qu'on a trouve le chemin le plus efficace, il faut le reconstruire grace
        //a la map des predecesseurs (chaque case dit que c'etait telle case le predecesseur)
        PositionAgent current = target;
        PositionAgent nextStep = target;
        while (predecessorMap.containsKey(current) && !predecessorMap.get(current).equals(start)) {
            nextStep = current;
            current = predecessorMap.get(current);
        }

        nextStep = current;
        
        if (!predecessorMap.containsKey(current) && !current.equals(start)) {
            return new AgentAction(AgentAction.STOP);
        }
        
        int x = nextStep.getX() - start.getX();
        int y = nextStep.getY() - start.getY();

        if (x == 1) {
            return new AgentAction(AgentAction.EAST);
        }
        else if (x == -1) {
            return new AgentAction(AgentAction.WEST);
        }
        else if (y == 1) {
            return new AgentAction(AgentAction.SOUTH);
        }
        else if (y == -1) {
            return new AgentAction(AgentAction.NORTH);
        }
        return new AgentAction(AgentAction.STOP);
    }

    private ArrayList<PositionAgent> getUnvisitedChildNode(PositionAgent node, Maze m) {
        ArrayList<PositionAgent> a = new ArrayList<PositionAgent>();
        if (!m.isWall(node.getX(), node.getY()+1)) {
            a.add(new PositionAgent(node.getX(), node.getY()+1, 0));
        }
        if (!m.isWall(node.getX(), node.getY()-1)) {
            a.add(new PositionAgent(node.getX(), node.getY()-1, 0));
        }
        if (!m.isWall(node.getX()+1, node.getY())) {
            a.add(new PositionAgent(node.getX()+1, node.getY(), 0));
        }
        if (!m.isWall(node.getX()-1, node.getY())) {
            a.add(new PositionAgent(node.getX()-1, node.getY(), 0));
        }
        return a;
    }
    
}
