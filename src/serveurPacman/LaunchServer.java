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
    
    public void retirerSession(SessionJeu session) {
        listeSessions.remove(session);
    }

    public synchronized void assignerClientASession(ConnectionToClient client, InitialisationPartieModele init) {
        SessionJeu sessionTrouvee = null;
        
        /*
         * On va venir gerer 3 cas pour assigner un client a la session
         * CAS 1 : Le joueur fait une partie sans room
         * CAS 2 : Le joueur cree une room
         * CAS 3 : Le joueur veut rejoindre une room
         */
        if (init.isRandom()) {
            for (int i = 0; i < listeSessions.size(); i++) {                
                if (listeSessions.get(i).isRandom() && 
                    !listeSessions.get(i).isPartieDemarree() && 
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
            	/*
            	 * Le joueur a lancer une partie sans room, mais on a pas trouve
            	 * de parties qui correspondent a ces criteres (difficulte, layout, nb de joueur)
            	*/
                try {
                    sessionTrouvee = new SessionJeu(init, this);
                    listeSessions.add(sessionTrouvee);
                } catch (Exception e) {}
            }
            
        } else if (init.isCreation()) {
            // CAS 2 : Le joueur veut créer une room privée
            try {
                sessionTrouvee = new SessionJeu(init, this);
                listeSessions.add(sessionTrouvee);
            } catch (Exception e) {
            }
            
        } else {
            // CAS 3 : Le joueur veut rejoindre une room privée existante
            for (int i = 0; i < listeSessions.size(); i++) {
                SessionJeu sessionActuelle = listeSessions.get(i);
                System.out.println(sessionActuelle.isRandom());
                if (!sessionActuelle.isRandom() && 
                    sessionActuelle.getRoomId() != null && 
                    sessionActuelle.getRoomId().equals(init.getRoomId()) &&
                    !sessionActuelle.isPartieDemarree() && 
                    sessionActuelle.getClientList().size() < sessionActuelle.getNombreJoueursAttendus()) {
                    /*
                     * Sur ces conditions, on se moque de savoir le niveau et la difficulte que le joueur a choisi
                     * vu qu'il a mis l'id exacte de la room, on part du principe qu'il est au courant des params
                     */
                    sessionTrouvee = sessionActuelle;
                    break;
                }
            }
        }
        
        client.setSession(sessionTrouvee);
        sessionTrouvee.ajouterClient(client);
    }
}