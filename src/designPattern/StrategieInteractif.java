package designPattern;

import game.PacmanGame;
import model.Agent;
import model.AgentAction;

public class StrategieInteractif implements StrategieAgent {
    // On initialise par défaut à STOP (4)
    private int lastActionDirection = AgentAction.STOP; 

    public StrategieInteractif() {
    }

    public void setLastActionDirection(int direction) {
        this.lastActionDirection = direction;
    }

    public AgentAction getAction(Agent agent, PacmanGame game) {
        // 1. On prépare l'action basée sur la touche pressée
        AgentAction action = new AgentAction(this.lastActionDirection);
        
        // 2. IMPORTANT : On réinitialise la direction immédiatement.
        // Ainsi, au prochain tour (step), si aucune nouvelle touche n'est pressée,
        // l'action sera STOP par défaut.
        this.lastActionDirection = AgentAction.STOP;

        // 3. On vérifie si le mouvement demandé est légal
        if (game.isLegalMove(agent, action)) {
            return action;
        } else {
            // Si le mouvement vers la flèche est impossible (mur), on s'arrête
            return new AgentAction(AgentAction.STOP);
        }
    }
}