package serveurPacman;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import com.google.gson.Gson;

import designPattern.StrategieInteractif;
import model.Agent;
import model.InitialisationPartieModele;
import model.Pacman;

public class ConnectionToClient {
    
    private BufferedReader in;
    private PrintWriter out;
    private Socket socket;
    private Agent pacman;
    private SessionJeu session;
    private LaunchServer serveur;
    private Gson gson = new Gson();

    public ConnectionToClient(Socket socket, LaunchServer serveur) throws IOException {
        this.socket = socket;
        this.serveur = serveur;
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        Thread read = new Thread() {
            public void run() {
                try {
                    String line;
                    boolean isInitialized = false;
                    
                    while((line = in.readLine()) != null) {                            
                    	/*
                         * Deux cas ou on recoit les donnees d'un client :
                         * Soit le client envoie des donnees pour initialiser une partie avec le niveau et la difficulte
                         * Soit le client envoie la donnees du mouvement qu'il realise sur une partie en cours
                         */
                        if (!isInitialized) {
                        	try {
                                InitialisationPartieModele init = gson.fromJson(line, InitialisationPartieModele.class);
                                serveur.assignerClientASession(ConnectionToClient.this, init);
                                isInitialized = true;
                                InitialisationPartieModele params = new InitialisationPartieModele(session.getNiveau(), session.getDifficulte(), session.getRoomId(), false, session.isRandom());
                                //on vient re-initialiser pour gerer le cas ou on rejoint une room avec une id, mais qu'on avait pas mis les meme params (parce que l'id est le meme)
                                write(gson.toJson(params));
                            } catch (Exception e) {
                            }
                        } 
                        else if (session != null && session.isPartieDemarree() && pacman != null) {
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
                    System.out.println("Client déconnecté.");
                    try { socket.close(); } catch (IOException ex) {} 
                    session.retirerClient(ConnectionToClient.this);
                } finally {
                    
                }
            }
        };
        read.start();
    }

    public void assignerPacman() {
        int pacmanAssignes = 0;
        for (int i = 0; i < session.getClientList().size(); i++) {
            ConnectionToClient client = session.getClientList().get(i);
    		if (client.pacman != null) {
				pacmanAssignes++;
    		}
        }
        
        int pacmansVu = 0;
        for (int i = 0; i < session.getVraiJeu().listeAgent.size(); i++) {
            if (session.getVraiJeu().listeAgent.get(i) instanceof Pacman) {
                if (pacmansVu == pacmanAssignes) {
                    this.pacman = session.getVraiJeu().listeAgent.get(i);
                    break;
                }
                pacmansVu++;
            }
        }
        
        if (pacmanAssignes + 1 == session.getNombreJoueursAttendus() && !session.isPartieDemarree()) {
        	session.demarrerPartie();
        }
    }
    
    public void write(String message) {
        out.println(message);
    }
    
    public Agent getPacman() {
        return pacman;
    }

    public void setSession(SessionJeu session) {
        this.session = session;
    }    
}