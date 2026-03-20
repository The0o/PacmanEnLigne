package vue;

import java.awt.BorderLayout;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

public class StatistiquesWindow {

    private JFrame jFrame;

    // Le constructeur prend maintenant le cookie en paramètre
    public StatistiquesWindow(String username, String sessionCookie) {
        jFrame = new JFrame("Historique des scores");
        jFrame.setSize(600, 400); // Un peu plus grand pour voir l'historique
        jFrame.setLocationRelativeTo(null); 
        
        JPanel panel = new JPanel(new BorderLayout());
        
        String nomAffiche = (username != null && !username.isEmpty()) ? username : "Joueur Inconnu";
        JLabel titre = new JLabel("Historique de " + nomAffiche, SwingConstants.CENTER);
        titre.setFont(new Font("Monospaced", Font.BOLD, 20));
        panel.add(titre, BorderLayout.NORTH);
        
        // Récupération des données en utilisant le cookie de session
        String statsTexte = recupererStatsDuServeur(sessionCookie);
        
        JLabel statsLabel = new JLabel("<html><center>" + statsTexte + "</center></html>", SwingConstants.CENTER);
        statsLabel.setFont(new Font("Monospaced", Font.PLAIN, 14));
        
        // On ajoute une barre de défilement (JScrollPane) au cas où l'historique est long
        JScrollPane scrollPane = new JScrollPane(statsLabel);
        scrollPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        jFrame.add(panel);
        jFrame.setVisible(true);
    }

    private String recupererStatsDuServeur(String sessionCookie) {
        // Si le cookie est absent, on ne peut pas interroger l'API
        if (sessionCookie == null || sessionCookie.isEmpty()) {
            return "Erreur : Vous n'êtes pas connecté (Aucune session trouvée).";
        }

        try {
            // L'URL de votre API
            URL url = new URL("http://localhost:8080/test/api/scores/history?limit=5&offset=0");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            
            // --- C'EST ICI QUE LA MAGIE OPÈRE ---
            // On injecte le cookie de session dans l'en-tête de la requête
            connection.setRequestProperty("Cookie", sessionCookie);

            int responseCode = connection.getResponseCode();
            
            if (responseCode >= 200 && responseCode < 300) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                
                // Pour l'instant, on affiche le JSON brut retourné par votre API. 
                // (On rajoute des balises <br> pour que le HTML aille à la ligne si besoin).
                String jsonResult = response.toString();
                return "Dernières parties :<br><br><div style='text-align: left;'>" + jsonResult + "</div>";
                
            } else if (responseCode == 401 || responseCode == 403) {
                return "Accès refusé. Votre session a peut-être expiré. (Erreur " + responseCode + ")";
            } else {
                return "Impossible de récupérer l'historique.<br>(Code erreur : " + responseCode + ")";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Erreur de connexion au serveur distant.";
        }
    }
}