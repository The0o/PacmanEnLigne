package serveurPacman;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;
import com.google.gson.Gson;

import designPattern.StrategieInteractif;
import game.PacmanGame;
import model.Agent;
import model.Fantome;
import model.GameStateModel;
import model.InitialisationPartieModele;
import model.Pacman;

public class LaunchServer {

    public static void main(String[] args) throws IOException {
        new LaunchServer(9081);
    }
    
    private CopyOnWriteArrayList<ConnectionToClient> clientList;
    private ServerSocket serverSocket;
    private Gson gson = new Gson();
    private boolean partieDemarree = false;
    private PacmanGame vraiJeu;
    private int nombreJoueursAttendus = 0;

    public LaunchServer(int port) throws IOException {
        clientList = new CopyOnWriteArrayList<ConnectionToClient>();
        serverSocket = new ServerSocket(port);

        while(true) {
            try {
                Socket s = serverSocket.accept();
                
                ConnectionToClient client = new ConnectionToClient(s);
                clientList.add(client);
                if (vraiJeu == null) {
                	//Premier joueur
                }
                else {
                	client.assignerPacman();
                }
                
            } catch(IOException e) { 
                e.printStackTrace();
            }
        }
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
                System.out.println("FIN PARTIE");
            });
            gameLoop.start();
    }

    private GameStateModel pacmanGameToGameStateModel(PacmanGame vraiJeu) {
        GameStateModel stateModel = new GameStateModel();
        stateModel.setMaze(vraiJeu.getMaze());
    	for (int i = 0; i < vraiJeu.listeAgent.size(); i++) {
    		if (vraiJeu.listeAgent.get(i).getClass().equals(Fantome.class)) {
    			stateModel.getPositionsFantomes().add(vraiJeu.listeAgent.get(i).getPosition());
    		}
    		else {
    			stateModel.getPositionsPacmans().add(vraiJeu.listeAgent.get(i).getPosition());
    		} 
    	}
        return stateModel;
    }
    
    private class ConnectionToClient {
        BufferedReader in;
        PrintWriter out;
        Socket socket;
        Agent pacman;

        ConnectionToClient(Socket socket) throws IOException {
            this.socket = socket;
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            Thread read = new Thread() {
                public void run() {
                    try {
                        String line;
                        while((line = in.readLine()) != null) {                            
                            /*
                             * Deux cas ou on recoit les donnees d'un client :
                             * Soit le client envoie des donnees pour initialiser une partie avec le niveau et la difficulte
                             * Soit le client envoie la donnees du mouvement qu'il realise sur une partie en cours
                             */
                            if (!partieDemarree && vraiJeu == null) {
                                try {
                                    InitialisationPartieModele init = gson.fromJson(line, InitialisationPartieModele.class);
                                    vraiJeu = new PacmanGame(1000, init.getChoixNiveau(), init.getDifficulte());
                                    vraiJeu.init();
                                    nombreJoueursAttendus = vraiJeu.getMaze().getInitNumberOfPacmans();
                                    assignerPacman();
;                                    
                                } catch (Exception e) {
                                }
                            } else if (partieDemarree && pacman != null) {
                            	try {
	                                int direction = Integer.parseInt(line);
	                            	System.out.println(direction);
	                                if (pacman.getStrategie() instanceof StrategieInteractif) {
	                                    ((StrategieInteractif) pacman.getStrategie()).setLastActionDirection(direction);
	                                }
                            	} catch (NumberFormatException e) {
                            		
                            	}
                            }
                        }
                    } catch(IOException e) { 
                        e.printStackTrace();
                        System.out.println("Client déconnecté.");
                        try { socket.close(); } catch (IOException ex) {} 
                        clientList.remove(ConnectionToClient.this);
                    }
                }
            };
            read.start();
        }

        public void assignerPacman() {
			int pacmanAssignes = 0;
        	for (int i = 0; i < clientList.size(); i++) {
        		ConnectionToClient client = clientList.get(i);
        		if (client.pacman != null) {
					pacmanAssignes++;
        		}
        	}
    		int pacmansVu = 0;
    		for (int i = 0; i < vraiJeu.listeAgent.size(); i++) {
                if (vraiJeu.listeAgent.get(i) instanceof Pacman) {
                    if (pacmansVu == pacmanAssignes) {
                        this.pacman = vraiJeu.listeAgent.get(i);
                        break;
                    }
                    pacmansVu++;
                }
    		}
            
    		if (pacmanAssignes + 1 == nombreJoueursAttendus && !partieDemarree) {
                demarrerPartie();
            }
		}

		public void write(String message) {
            out.println(message);
        }
    }

    public void sendToOne(int index, String message) throws IndexOutOfBoundsException {
        clientList.get(index).write(message);
    }

    public void sendToAll(String message) {
        for(ConnectionToClient client : clientList)
            client.write(message);
    }
}