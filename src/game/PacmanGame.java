package game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import designPattern.FantomeFactory;
import designPattern.PacmanFactory;
import designPattern.StrategieAgent;
import designPattern.StrategieDifficulte;
import designPattern.StrategieFurthest;
import designPattern.StrategieInteractif;
import designPattern.StrategieShortest;
import model.Agent;
import model.AgentAction;
import model.Fantome;
import model.Maze;
import model.Pacman;
import model.PositionAgent;

public class PacmanGame extends Game {
    
    public Maze maze;
    public String layoutFile;
    public ArrayList<Agent> listeAgent = new ArrayList<>();
    public int capsulteTimer = 0;
    public double difficultePourcentage;
    private Map<Pacman, Integer> nourritureMangeeParPacman = new HashMap<>();

    public PacmanGame(int maxTurn, String layout, double difficultePourcentage) throws Exception {
        super(maxTurn);
        this.maze = new Maze(layout);
        this.layoutFile = layout;
        this.difficultePourcentage = difficultePourcentage;
    }

    public Maze getMaze() {
        return this.maze;
    }

    public int getCapsuleTimer() {
        return this.capsulteTimer;
    }

    @Override
    public void initializeGame() {
        this.listeAgent.clear();
        try {
            this.maze = new Maze(this.layoutFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        PacmanFactory pacmanFactory = new PacmanFactory();
        for (PositionAgent agent : this.maze.getPacman_start()) {
            Pacman pacman = (Pacman) pacmanFactory.createAgent(agent);
            pacman.setStrategie(new StrategieInteractif());
            listeAgent.add(pacman);
            nourritureMangeeParPacman.put(pacman, 0);
        }

        FantomeFactory fantomeFactory = new FantomeFactory();
        for (PositionAgent agent : this.maze.getGhosts_start()) {
            Agent fantome = fantomeFactory.createAgent(agent);
            fantome.setStrategie(new StrategieShortest());
            listeAgent.add(fantome);
        }
    }

    /*
    Fonction pour que les agents ne fassent pas de mouvement illegal comme aller dans un mur
    */
    public boolean isLegalMove(Agent agent, AgentAction agentAction) {
        int x = agent.getPosition().getX() + agentAction.get_vx();
        int y = agent.getPosition().getY() + agentAction.get_vy();
        return !this.maze.isWall(x, y);
    }

    public void moveAgent(Agent agent, AgentAction agentAction) {
        int x = agent.getPosition().getX() + agentAction.get_vx();
        int y = agent.getPosition().getY() + agentAction.get_vy();
        agent.setPosition(x, y);
        agent.getPosition().setDir(agentAction.get_direction());
    }

    @Override
    public void gameOver() {
        if (isBoardClear()) {
            System.out.println("VICTOIRE");
        }
        else {
            System.out.println("DEFAITE");
        }
    }

    private boolean isBoardClear() {
        for (int x = 0; x < maze.getSizeX(); x++) {
            for (int y = 0; y < maze.getSizeY(); y++) {
                if (maze.isCapsule(x, y) || maze.isFood(x, y)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean gameContinue() {
        int pacmanRestant = 0;
        for (Agent agent : listeAgent) {
            if (agent instanceof Pacman && agent.getPosition().getX() != -1) {
                pacmanRestant++;
            }
        }
        if (pacmanRestant == 0) {
            return false;
        }
        if (isBoardClear()) {
            return false;
        }
        return true;
    }

    @Override
    public void takeTurn() {

        if (capsulteTimer > 0) {
            capsulteTimer--;
        }

        ArrayList<Pacman> pacmans = new ArrayList<>();
        ArrayList<Fantome> fantomes = new ArrayList<>();
        //Comme Fantome et Pacman sont des agent on itere sur eux
        for (Agent agent : this.listeAgent) {
            if (agent instanceof Fantome) {
                //si c'est des fantomes, on applique une strategie differente si ils peuvent
                //se faire manger ou non
                StrategieAgent strategie;

                if (this.capsulteTimer > 0) {
                    strategie = new StrategieFurthest();
                }
                else {
                    strategie = new StrategieShortest();
                }
                agent.setStrategie(new StrategieDifficulte(strategie, this.difficultePourcentage));
            }
            if (agent.getPosition().getX() != -1) {
                if (agent instanceof Pacman) {
                    pacmans.add((Pacman) agent);
                }
                else {
                    fantomes.add((Fantome) agent);
                }
                AgentAction agentAction = agent.getStrategie().getAction(agent, this);
                this.moveAgent(agent, agentAction);
                if (agent instanceof Pacman) {
                    //on regarde si notre pacman est sur une case avec de la nourriture ou une gomme pour l'enlever
                    if (this.maze.isFood(agent.getPosition().getX(), agent.getPosition().getY())) {
                        this.maze.setFood(agent.getPosition().getX(), agent.getPosition().getY(), false);
                        incrementerNourritureMangee((Pacman) agent);
                    }
                    if (this.maze.isCapsule(agent.getPosition().getX(), agent.getPosition().getY())) {
                        this.maze.setCapsule(agent.getPosition().getX(), agent.getPosition().getY(), false);
                        this.capsulteTimer = 20;
                    }
                }
            }
        }

        //on va checker si les pacmans ou fantomes se sont fait manger
        for (Pacman pacman : pacmans) {
            for (Fantome fantome : fantomes) {
                if (pacman.getPosition().equals(fantome.getPosition()) ||
                    pacman.getPosition().equals(fantome.getOldPosition()) && fantome.getPosition().equals(pacman.getOldPosition())) {
                    if (this.capsulteTimer > 0) {
                        fantome.setPosition(-1, -1);
                    }
                    else {
                        pacman.setPosition(-1, -1);
                    }
                }
            }
        }
    }

    public PositionAgent getPacmanPosition() {
        for (Agent agent : this.listeAgent) {
            if (agent instanceof Pacman) {
                if (agent.getPosition().getX() != -1) {
                    return agent.getPosition();
                    //attention on ne gere que le premier pacman
                    //si y'a plusieurs pacman c'est pas ouf
                }
            }
        }
        return new PositionAgent(-1, -1, 0);
    }

    private void incrementerNourritureMangee(Pacman pacman) {
        Integer total = nourritureMangeeParPacman.get(pacman);
        if (total == null) {
            total = 0;
        }
        nourritureMangeeParPacman.put(pacman, total + 1);
    }

    public int getNourritureMangeeParPacman(Pacman pacman) {
        Integer total = nourritureMangeeParPacman.get(pacman);
        return total == null ? 0 : total;
    }
    
}
