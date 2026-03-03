import clientMain.GameClient;
import game.Game;
import game.PacmanGame;
import vue.GameLauncher;
import vue.ViewPacmanGame;

public class ClientLauncher {

    public static void main(String[] args) throws Exception {
    	//Code non fonctionnel pour l'instant, je construit juste les esquisses de l'architecture
    	ViewPacmanGame viewGame = new ViewPacmanGame();
        GameClient gameClient = new GameClient("localhost", 9081, viewGame);
    }

}
