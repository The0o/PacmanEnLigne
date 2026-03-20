package vue;
import javax.swing.JOptionPane;

import clientMain.GameClient;
import game.PacmanGame;

public class GameLauncherEnLigne extends GameLauncher {
    
    @Override
    public void lancerJeu() throws Exception {
        // 1. Demander l'adresse IP à l'utilisateur (par défaut localhost si on joue sur le même PC)
        String ipServeur = JOptionPane.showInputDialog(null, "Entrez l'adresse IP du serveur :", "localhost");
        
        if (ipServeur == null || ipServeur.trim().isEmpty()) {
            return; // Le joueur a annulé
        }
        
        int difficulte = choixDifficulte.getSelectedIndex();
        double diff = 0.4;
        if (difficulte == 0) {
            diff = 0.1;
        } else if (difficulte == 2) {
            diff = 0.7;
        } else if (difficulte == 3) {
            diff = 0.9;
        }
        
        try {
        	new GameClient(ipServeur, 9081, null, "src/layouts/" + choixNiveau.getSelectedItem(), diff, sessionCookie);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Impossible de se connecter au serveur " + ipServeur);
        }
    }
}