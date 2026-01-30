package designPattern;

import game.PacmanGame;
import model.Agent;
import model.AgentAction;

public class StrategieDifficulte implements StrategieAgent {

    public StrategieAgent strategieIntelligente; //on a shortest ou furthest pour le meme fantome donc
    //on peut pas mettre directement la strategie voulu contrairement a l'aleatoire
    public StrategieAleatoire strategieAleatoire;
    public double pourcentageIntelligence;

    public StrategieDifficulte(StrategieAgent strategie, double difficultePourcentage) {
        this.strategieIntelligente = strategie;
        this.pourcentageIntelligence = difficultePourcentage;
        this.strategieAleatoire = new StrategieAleatoire();
    }

    @Override
    public AgentAction getAction(Agent agent, PacmanGame pacmanGame) {
        //le principe de cette strategie reside sur la difficulte choisi de l'utilisateur
        //Ex : en mode difficile, le fantome à 9 chances sur 10 de prendre le meilleur chemin possible
        //(un peu plus en realite, vu que le 1/10 à 1 chance sur 4 de prendre le bon chemin par chance)
        //donc 0.925
        if (Math.random() < pourcentageIntelligence) {
            return strategieIntelligente.getAction(agent, pacmanGame);
        }
        else {
            return strategieAleatoire.getAction(agent, pacmanGame);
        }
    }
    
}
