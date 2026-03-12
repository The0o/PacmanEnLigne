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
        
        try {
            vraiJeu = new PacmanGame(1000, "layouts/test.lay", 0.1);
            vraiJeu.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        int nombreJoueursAttendus = vraiJeu.getMaze().getInitNumberOfPacmans();
        int indexPacmanActuel = 0;

        while(true) {
            try {
                Socket s = serverSocket.accept();
                
                Agent pacmanTrouve = null;
                int pacmansVu = 0;
                
                //on associe le joueur a un pacman
                for (int i = 0; i < vraiJeu.listeAgent.size(); i++) {
                    if (vraiJeu.listeAgent.get(i) instanceof Pacman) {
                        if (pacmansVu == indexPacmanActuel) {
                            pacmanTrouve = vraiJeu.listeAgent.get(i);
                            break;
                        }
                        pacmansVu++;
                    }
                }
                
                if (pacmanTrouve != null) {
                    clientList.add(new ConnectionToClient(s, pacmanTrouve));
                    indexPacmanActuel++;

                    if (indexPacmanActuel == nombreJoueursAttendus && !partieDemarree) {
                        demarrerPartie();
                    }
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

        ConnectionToClient(Socket socket, Agent pacman) throws IOException {
            this.socket = socket;
            this.pacman = pacman;
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            Thread read = new Thread() {
                public void run() {
                    try {
                        String line;
                        while((line = in.readLine()) != null) {
                            String texte = gson.fromJson(line, String.class);
                            
                            if (partieDemarree && pacman != null) {
                                int direction = Integer.parseInt(texte);
                                if (pacman.getStrategie() instanceof StrategieInteractif) {
                                    ((StrategieInteractif) pacman.getStrategie()).setLastActionDirection(direction);
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