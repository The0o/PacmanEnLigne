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
import model.GameStateModel;
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

    public LaunchServer(int port) throws IOException {
        clientList = new CopyOnWriteArrayList<ConnectionToClient>();
        serverSocket = new ServerSocket(port);

        while(true) {
            try {
                Socket s = serverSocket.accept();
                clientList.add(new ConnectionToClient(s));
            } catch(IOException e) { 
                e.printStackTrace();
            }
        }
    }
    
    public void demarrerPartie() {
    	//Modifier cette fonction pour qu'elle fonctionne dans tout les cas, pour
    	//l'instant on force la diff, le layout, la vitesse, etc...
        try {
            vraiJeu = new PacmanGame(1000, "layouts/test.lay", 0.4);
            vraiJeu.init();
            partieDemarree = true;
            
            Thread gameLoop = new Thread(() -> {
                while(vraiJeu.gameContinue()) {
                    try {
                        vraiJeu.step();
                        GameStateModel gameState = pacmanGameToGameStateModel(vraiJeu);
                        sendToAll(gson.toJson(gameState));
                        Thread.sleep(1000);
                        
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("FIN PARTIE");
            });
            gameLoop.start();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private GameStateModel pacmanGameToGameStateModel(PacmanGame vraiJeu) {
        GameStateModel stateModel = new GameStateModel();
        stateModel.setMaze(vraiJeu.getMaze());
        stateModel.setListeAgent(vraiJeu.listeAgent);
        return stateModel;
    }
    
    private class ConnectionToClient {
        BufferedReader in;
        PrintWriter out;
        Socket socket;

        ConnectionToClient(Socket socket) throws IOException {
            this.socket = socket;
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            Thread read = new Thread() {
                public void run() {
                    try {
                        String line;
                        while((line = in.readLine()) != null) {
                            String texte = gson.fromJson(line, String.class);
                            System.out.println("Donnees recu chef");
                            if (!partieDemarree) {
                            	//tres basique pour l'instant, des que le serveur reçoit une donnee
                            	//il lance la partie (donc pas de multijoueur pour le moment)
                            	demarrerPartie();
                            }
                            else {
                            	int direction = Integer.parseInt(texte);
                            	
                            	for (Agent agent : vraiJeu.listeAgent) {
                                    if (agent instanceof Pacman && agent.getStrategie() instanceof StrategieInteractif) {
                                    	//pas beau du tout, ça bouge tout les pacmans dans la meme direction, autrement
                                    	//dit le multijoueur ca va etre nul -> A MODIFIER
                                        ((StrategieInteractif) agent.getStrategie()).setLastActionDirection(direction);
                                    }
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

        public void write(String message) {
            out.println(gson.toJson(message));
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