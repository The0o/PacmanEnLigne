package serveurPacman;

import java.util.concurrent.CopyOnWriteArrayList;
import com.google.gson.Gson;
import game.PacmanGame;
import model.Agent;
import model.Fantome;
import model.GameStateModel;
import model.InitialisationPartieModele;

public class SessionJeu {
    
    private CopyOnWriteArrayList<ConnectionToClient> clientList;
    private boolean partieDemarree = false;
    private PacmanGame vraiJeu;
    private int nombreJoueursAttendus;
    private String niveau;
    private double difficulte;
    private String roomId; 
    private boolean isRandom;
    private int nombreFoodInitial;
    private Gson gson = new Gson();

    public SessionJeu(InitialisationPartieModele init) throws Exception {
        this.clientList = new CopyOnWriteArrayList<>();
        this.niveau = init.getChoixNiveau();
        this.difficulte = init.getDifficulte();
        this.roomId = init.getRoomId(); 
        this.isRandom = init.isRandom();
        
        this.vraiJeu = new PacmanGame(1000, this.niveau, this.difficulte);
        this.vraiJeu.init();
        this.nombreJoueursAttendus = this.vraiJeu.getMaze().getInitNumberOfPacmans();
        this.nombreFoodInitial = compterFoodRestante();
    }

    public void demarrerPartie() {
        partieDemarree = true;
        
        Thread gameLoop = new Thread(() -> {
            while(vraiJeu.gameContinue()) {
                try {
                    vraiJeu.step();
                    GameStateModel gameState = pacmanGameToGameStateModel(vraiJeu);
                    sendToAll(gson.toJson(gameState));
                    Thread.sleep(100);
                    
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            sendScore();
        });
        gameLoop.start();
    }

    public void sendScore() {
		int nbFoodRestante = compterFoodRestante();
		int nbTour = vraiJeu.turn;
		int nbFoodMangee = Math.max(0, nombreFoodInitial - nbFoodRestante);

		int score = nbFoodMangee * 100 - nbTour;
		if (nbFoodRestante == 0) {
			score += 500;
		}
		score = Math.max(0, score);

		for (ConnectionToClient client : clientList) {
			client.envoyerScore(score);
		}
	}

	private int compterFoodRestante() {
		int nbFood = 0;
		for (int i = 0; i < vraiJeu.getMaze().getSizeX(); i++) {
			for (int j = 0; j < vraiJeu.getMaze().getSizeY(); j++) {
				if (vraiJeu.getMaze().isFood(i, j)) {
					nbFood++;
				}
			}
		}
		return nbFood;
	}

	private GameStateModel pacmanGameToGameStateModel(PacmanGame vraiJeu) {
        GameStateModel stateModel = new GameStateModel();
        stateModel.setMaze(vraiJeu.getMaze());
        for (int i = 0; i < vraiJeu.listeAgent.size(); i++) {
            Agent agent = vraiJeu.listeAgent.get(i);
            if (agent.getClass().equals(Fantome.class)) {
                stateModel.getPositionsFantomes().add(agent.getPosition());
            } else {
                stateModel.getPositionsPacmans().add(agent.getPosition());
                
                // NOUVEAU : Récupération du pseudo associé à l'agent
                String pseudo = "Bot"; 
                for (ConnectionToClient client : clientList) {
                    if (client.getPacman() == agent) {
                        if (client.getUsername() != null && !client.getUsername().isEmpty()) {
                            pseudo = client.getUsername();
                        } else {
                            pseudo = "Joueur";
                        }
                        break;
                    }
                }
                stateModel.getPacmansUsernames().add(pseudo);
            } 
        }
		if (vraiJeu.capsulteTimer > 0) {
			stateModel.setEffraye(true);
		} else {
			stateModel.setEffraye(false);
		}
        return stateModel;
    }

    public void ajouterClient(ConnectionToClient client) {
        clientList.add(client);
        client.assignerPacman();
        GameStateModel stateModel = pacmanGameToGameStateModel(vraiJeu);
        client.write(gson.toJson(stateModel));
    }

    public void retirerClient(ConnectionToClient client) {
        clientList.remove(client);
    }
    
    public void sendToOne(int index, String message) throws IndexOutOfBoundsException {
        clientList.get(index).write(message);
    }

    public void sendToAll(String message) {
        for(ConnectionToClient client : clientList) {
            client.write(message);
        }
    }

	public boolean isPartieDemarree() { return partieDemarree; }
	public String getNiveau() { return niveau; }
	public double getDifficulte() { return difficulte; }
    public String getRoomId() { return roomId; }
    public boolean isRandom() { return isRandom; }
	public CopyOnWriteArrayList<ConnectionToClient> getClientList() { return clientList; }
	public int getNombreJoueursAttendus() { return nombreJoueursAttendus; }
	public PacmanGame getVraiJeu() { return vraiJeu; }
}