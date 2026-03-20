package serveurPacman;

import java.util.concurrent.CopyOnWriteArrayList;
import com.google.gson.Gson;
import game.PacmanGame;
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
            System.out.println("FIN PARTIE");
        });
        gameLoop.start();
    }

    public void sendScore() {
		int nbFood = 0;
		for (int i = 0; i < vraiJeu.getMaze().getSizeX(); i++) {
			for (int j = 0; i < vraiJeu.getMaze().getSizeY(); j++) {
				if (vraiJeu.getMaze().isFood(i, j)) {
					nbFood++;
				}
			}
		}
		int nbTour = vraiJeu.turn;
		
		//Pour calculer le score, on va faire pour l'instant simple (et on complexifiera eventuellement plus tard si on souhaite)
		//on va utiliser le nombre de tour effectue par le joueur, et le nombre de food qu'il y avait
		//C'est vraiment simple et pas tres precis mais au moins on a le score
		int score = (nbFood-nbTour)/nbTour;
		//appel API :
	}

	private GameStateModel pacmanGameToGameStateModel(PacmanGame vraiJeu) {
        GameStateModel stateModel = new GameStateModel();
        stateModel.setMaze(vraiJeu.getMaze());
        for (int i = 0; i < vraiJeu.listeAgent.size(); i++) {
            if (vraiJeu.listeAgent.get(i).getClass().equals(Fantome.class)) {
                stateModel.getPositionsFantomes().add(vraiJeu.listeAgent.get(i).getPosition());
            } else {
                stateModel.getPositionsPacmans().add(vraiJeu.listeAgent.get(i).getPosition());
            } 
        }
        return stateModel;
    }

    public void ajouterClient(ConnectionToClient client) {
        clientList.add(client);
        client.assignerPacman();
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

	public boolean isPartieDemarree() {
		return partieDemarree;
	}

	public String getNiveau() {
		return niveau;
	}

	public double getDifficulte() {
		return difficulte;
	}

    public String getRoomId() {
        return roomId;
    }
    
    public boolean isRandom() {
        return isRandom;
    }

	public CopyOnWriteArrayList<ConnectionToClient> getClientList() {
		return clientList;
	}

	public int getNombreJoueursAttendus() {
		return nombreJoueursAttendus;
	}

	public PacmanGame getVraiJeu() {
		return vraiJeu;
	}
    
}