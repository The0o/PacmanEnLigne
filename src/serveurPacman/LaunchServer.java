package serveurPacman;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;
import model.InitialisationPartieModele;

public class LaunchServer {

    private CopyOnWriteArrayList<SessionJeu> listeSessions;
    private ServerSocket serverSocket;

    public static void main(String[] args) throws IOException {
        new LaunchServer(9081);
    }

    public LaunchServer(int port) throws IOException {
        listeSessions = new CopyOnWriteArrayList<>();
        serverSocket = new ServerSocket(port);

        while(true) {
            try {
                Socket s = serverSocket.accept();
                
                new ConnectionToClient(s, this);

            } catch(IOException e) { 
                e.printStackTrace();
            }
        }
    }

    public synchronized void assignerClientASession(ConnectionToClient client, InitialisationPartieModele init) {
        SessionJeu sessionTrouvee = null;
        
        for (int i = 0; i < listeSessions.size(); i++) {
            if (!listeSessions.get(i).isPartieDemarree() && 
            	 listeSessions.get(i).getClientList().size() < listeSessions.get(i).getNombreJoueursAttendus() &&
            	 listeSessions.get(i).getNiveau().equals(init.getChoixNiveau()) &&
            	 listeSessions.get(i).getDifficulte() == init.getDifficulte()) {
                /*pour assigner a la meme session, il faut que : 
                 * la partie de la session ne soit pas demaree (plus de la securite ca)
                 * que la session est le meme niveau que celui selectionne par le joueur
                 * que la session est la meme difficulte que celui selectionne par le joueur
                 * et enfin que le nombre de pacman attendu de la session est inferieur au nombre de pacman actuels dans la session
            	*/
                sessionTrouvee = listeSessions.get(i);
                break;
            }
        }
        
        if (sessionTrouvee == null) {
            try {
            	//si aucune session correspond au critere au dessus, on cree juste une nouvelle session
                sessionTrouvee = new SessionJeu(init);
                listeSessions.add(sessionTrouvee);
            } catch (Exception e) {
            }
        }
        
        client.setSession(sessionTrouvee);
        sessionTrouvee.ajouterClient(client);
    }
}