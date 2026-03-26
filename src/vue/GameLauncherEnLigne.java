package vue;
import javax.swing.JOptionPane;

import clientMain.GameClient;
import game.PacmanGame;

public class GameLauncherEnLigne extends GameLauncher {
    
    @Override
    public void lancerJeu() throws Exception {
        /*String ipServeur = JOptionPane.showInputDialog(null, "Entrez l'adresse IP du serveur :", "localhost");
        
        if (ipServeur == null || ipServeur.trim().isEmpty()) {
            return;
        }*/
       String ipServeur = "46.101.67.203";
        
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
            // NOUVEAU : Passage de usernameConnecte
        	new GameClient(ipServeur, 9081, null, "src/layouts/" + choixNiveau.getSelectedItem(), diff, sessionCookie, usernameConnecte);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Impossible de se connecter au serveur " + ipServeur);
        }
    }
}