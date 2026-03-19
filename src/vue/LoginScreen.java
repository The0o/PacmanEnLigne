package vue;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class LoginScreen {

    public LoginScreen(boolean enLigne) {
        JFrame frame = new JFrame("Connexion - Pacman");
        frame.setSize(350, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(3, 2, 10, 10)); // Grille de 3 lignes, 2 colonnes

        // Composants de l'interface
        JLabel emailLabel = new JLabel(" Email :");
        JTextField emailField = new JTextField();

        JLabel passLabel = new JLabel(" Mot de passe :");
        JPasswordField passField = new JPasswordField();

        JButton loginButton = new JButton("Se connecter");

        // Ajout des composants à la fenêtre
        frame.add(emailLabel);
        frame.add(emailField);
        frame.add(passLabel);
        frame.add(passField);
        frame.add(new JLabel()); // Espace vide pour aligner le bouton à droite
        frame.add(loginButton);

        // Action lors du clic sur le bouton "Se connecter"
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                String password = new String(passField.getPassword());

                // VERIFICATION DES IDENTIFIANTS
                // Ici, c'est un test "en dur". Dans un vrai jeu, il faudrait interroger 
                // une base de données ou un fichier contenant les joueurs inscrits.
                if (verifierIdentifiants(email, password)) {
                    JOptionPane.showMessageDialog(frame, "Connexion réussie !");
                    frame.dispose(); // Ferme la fenêtre de connexion
                    
                    // Lance le bon menu en fonction du mode choisi
                    if (enLigne) {
                        new GameLauncherEnLigne();
                    } else {
                        new GameLauncher();
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Email ou mot de passe incorrect.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        frame.setLocationRelativeTo(null); // Centre la fenêtre sur l'écran
        frame.setVisible(true);
    }

    // Méthode simulant la vérification (à remplacer par une vraie logique BDD/Fichier)
    private boolean verifierIdentifiants(String email, String password) {
        return email.equals("test@gmail.com") && password.equals("test");
    }
}