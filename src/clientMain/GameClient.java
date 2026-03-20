package clientMain;

import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import com.google.gson.Gson;



import game.Game;
import game.PacmanGame;
import model.GameStateModel;
import model.InitialisationPartieModele;
import vue.ViewPacmanGame;

public class GameClient {
    private ConnectionToServer server;
    private Socket socket;
    private Gson gson = new Gson();
    private ViewPacmanGame viewGame;
    
    public GameClient(String IPAdress, int port, ViewPacmanGame viewGame, String choixNiveau, double difficulte) throws IOException {
    		this(IPAdress, port, viewGame, choixNiveau, difficulte, null);
    }

    public GameClient(String IPAdress, int port, ViewPacmanGame viewGame, String choixNiveau, double difficulte, String sessionCookie) throws IOException {
    	this.viewGame = viewGame;
    	this.getInputDirection();
    	this.startClient(IPAdress, port);
    	InitialisationPartieModele jeuParamInit = new InitialisationPartieModele(choixNiveau, difficulte, sessionCookie);
        this.send(gson.toJson(jeuParamInit));
    }
    
    public GameClient(String IPAdress, int port, ViewPacmanGame viewGame, String choixNiveau, double difficulte, String roomId, boolean isCreation, boolean isRandom) throws IOException {
    		this(IPAdress, port, viewGame, choixNiveau, difficulte, roomId, isCreation, isRandom, null);
    }

    public GameClient(String IPAdress, int port, ViewPacmanGame viewGame, String choixNiveau, double difficulte, String roomId, boolean isCreation, boolean isRandom, String sessionCookie) throws IOException {
    	this.viewGame = viewGame;
    	this.getInputDirection();
    	this.startClient(IPAdress, port);
    	InitialisationPartieModele jeuParamInit = new InitialisationPartieModele(choixNiveau, difficulte, roomId, isCreation, isRandom, sessionCookie);
        this.send(gson.toJson(jeuParamInit));
    }
    
    public GameClient(String IPAdress, int port) throws IOException {
    	this.startClient(IPAdress, port);
    }
    
    public void startClient(String IPAddress, int port) throws IOException {
        socket = new Socket(IPAddress, port);
        server = new ConnectionToServer(socket);
    }

    private class ConnectionToServer {
        BufferedReader in;
        PrintWriter out;
        Socket socket;

        ConnectionToServer(Socket socket) throws IOException {
            this.socket = socket;
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            Thread read = new Thread() {
                public void run() {
                    try {
                        String line;
                        while((line = in.readLine()) != null) {
                            
                            if (line.contains("maze")) {
                                //si c'est maze, ca veut dire qu'on cherche a lancer/initialiser une partie
                            	//donc on va envoyer le plateau a l'utilisateur
                                GameStateModel etatDuJeu = gson.fromJson(line, GameStateModel.class);
                                
                                if (viewGame == null) {
                                    viewGame = new ViewPacmanGame(etatDuJeu.getMaze());
                                    getInputDirection();
                                }
                                viewGame.actualiserClient(etatDuJeu);
                            }
                        }
                    } catch(IOException e) { 
                        e.printStackTrace(); 
                    }
                }
            };
            read.start();
        }

        private void write(String obj) {
        	out.println(obj);
        }
    }
    
    private void getInputDirection() {
        if (this.viewGame != null && this.viewGame.getJFrame() != null) {
            
            this.viewGame.getJFrame().addKeyListener(new java.awt.event.KeyAdapter() {
                @Override
                public void keyPressed(java.awt.event.KeyEvent e) {
                    int key = e.getKeyCode();
                    if (key == java.awt.event.KeyEvent.VK_Z || key == java.awt.event.KeyEvent.VK_UP) {
                    	send("0");
                    } else if (key == java.awt.event.KeyEvent.VK_S || key == java.awt.event.KeyEvent.VK_DOWN) {
                        send("1");
                    } else if (key == java.awt.event.KeyEvent.VK_D || key == java.awt.event.KeyEvent.VK_RIGHT) {
                        send("2");
                    } else if (key == java.awt.event.KeyEvent.VK_Q || key == java.awt.event.KeyEvent.VK_LEFT) {
                        send("3");
                    }
                }
            });
            this.viewGame.getJFrame().requestFocus();
        }
    }


    public void send(String obj) {
        server.write(obj);
    
    }
}
