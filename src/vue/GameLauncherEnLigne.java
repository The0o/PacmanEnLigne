package vue;
import clientMain.GameClient;

public class GameLauncherEnLigne extends GameLauncher {
    
    public void lancerJeu() throws Exception {
        String choixFichier = (String) choixNiveau.getSelectedItem();
        String path = "layouts/" + choixFichier;

        int difficulte = choixDifficulte.getSelectedIndex();
        double diff = 0.4;
        if (difficulte == 0) {
            diff = 0.1;
        } else if (difficulte == 2) {
            diff = 0.7;
        } else if (difficulte == 3) {
            diff = 0.9;
        }
        //WARNING : pour l'instant ça gere rien du tout, et encore moins la fonction en ligne.
        //Sur cette classe (le GameLauncher), il manque de pouvoir se connecter à plusieurs, faire des rooms, partie en ligne aleatoire, etc
    	ViewPacmanGame viewGame = new ViewPacmanGame();
        GameClient gameClient = new GameClient("localhost", 9081, viewGame);
    }


}
