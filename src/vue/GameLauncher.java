package vue;

import java.awt.Desktop;
import java.net.URI;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import clientMain.GameClient;
import controller.ControllerPacmanGame;
import game.PacmanGame;

public class GameLauncher {

    protected JComboBox<String> choixNiveau;
    protected JComboBox<String> choixDifficulte;
    protected JTextField roomIdField;
    public static Clip clip; 
    private static final String LOGIN_API_URL = "http://localhost:8080/test/api/auth/login";
    
    // On rend la fenêtre et le fond accessibles aux autres méthodes  http://localhost:8080/test/TestDBServlet
    protected JFrame jFrame;
    protected JLabel backgroundLabel;
    protected String sessionCookie;
    protected String usernameConnecte;

    public GameLauncher() {
        jFrame = new JFrame();
        jFrame.setTitle("Pacman");

        
        int largeurFenetre = 800;
        int hauteurFenetre = 600;
        
        //-------------IMAGE BACKGROUND---------------
        ImageIcon image = loadImageIcon("/image/pacmanImage.jpg", "src/image/pacmanImage.jpg", "image/pacmanImage.jpg");
        if (image.getIconWidth() != -1) {
        	java.awt.Image img = image.getImage();
            java.awt.Image newImg = img.getScaledInstance(largeurFenetre, hauteurFenetre, java.awt.Image.SCALE_SMOOTH);
            backgroundLabel = new JLabel(new ImageIcon(newImg));
        }
        else {
            backgroundLabel = new JLabel();
        }
        
        
        jFrame.setPreferredSize(new Dimension(largeurFenetre, hauteurFenetre));
        backgroundLabel.setLayout(new BoxLayout(backgroundLabel, BoxLayout.Y_AXIS));
        jFrame.setContentPane(backgroundLabel);

        // Au démarrage, on lance la musique et on affiche l'écran de connexion
        launchMusic();
        afficherEcranConnexion();

        jFrame.pack();
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);
    }

    // --- NOUVELLE MÉTHODE : AFFICHER LA CONNEXION ---
    public void afficherEcranConnexion() {
        backgroundLabel.removeAll(); // Vide l'écran

        JLabel titre = new JLabel("CONNEXION");
        titre.setFont(new Font("Monospaced", Font.BOLD, 30));
        titre.setForeground(java.awt.Color.WHITE);
        titre.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel usernameLabel = new JLabel("Username :");
        usernameLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        usernameLabel.setForeground(java.awt.Color.WHITE);
        usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JTextField usernameField = new JTextField();
        usernameField.setMaximumSize(new Dimension(250, 30));

        JLabel passLabel = new JLabel("Mot de passe :");
        passLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        passLabel.setForeground(java.awt.Color.WHITE);
        passLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPasswordField passField = new JPasswordField();
        passField.setMaximumSize(new Dimension(250, 30));

        JButton loginButton = new JButton("SE CONNECTER");
        loginButton.setFont(new Font("Monospaced", Font.BOLD, 18));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        
     // --- NOUVEAU : LIEN "CRÉER UN COMPTE" ---
        JLabel creerCompteLabel = new JLabel("<html><a href=\"\">Créer un compte</a></html>");
        creerCompteLabel.setFont(new Font("Monospaced", Font.PLAIN, 14));
        creerCompteLabel.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Curseur "main"
        creerCompteLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        creerCompteLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        
        
     // Ajout de l'événement au clic
        creerCompteLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                afficherEcranInscription(); // On appelle la nouvelle méthode pour afficher l'écran d'inscription
            }
        });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText().trim();
                String password = new String(passField.getPassword());

                if (verifierIdentifiants(username, password)) {
                    // Si OK, on charge le menu principal à la place
                    afficherMenuPrincipal(); 
                } else {
                    JOptionPane.showMessageDialog(jFrame, "Connexion echouee. Verifiez le username, le mot de passe, ou le serveur web.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Ajout des éléments avec des espaces (RigidArea)
        backgroundLabel.add(Box.createRigidArea(new Dimension(0, 50)));
        backgroundLabel.add(titre);
        backgroundLabel.add(Box.createRigidArea(new Dimension(0, 30)));
        backgroundLabel.add(usernameLabel);
        backgroundLabel.add(usernameField);
        backgroundLabel.add(Box.createRigidArea(new Dimension(0, 15)));
        backgroundLabel.add(passLabel);
        backgroundLabel.add(passField);
        backgroundLabel.add(Box.createRigidArea(new Dimension(0, 30)));
        backgroundLabel.add(loginButton);
        backgroundLabel.add(Box.createRigidArea(new Dimension(0, 15))); // Espace avant le lien
        backgroundLabel.add(creerCompteLabel);

        // Rafraîchissement de la fenêtre
        backgroundLabel.revalidate();
        backgroundLabel.repaint();
    }
    
 // --- NOUVELLE MÉTHODE : AFFICHER LA CRÉATION DE COMPTE ---
    public void afficherEcranInscription() {
        backgroundLabel.removeAll(); // Vide l'écran

        JLabel titre = new JLabel("INSCRIPTION");
        titre.setFont(new Font("Monospaced", Font.BOLD, 30));
        titre.setForeground(java.awt.Color.WHITE);
        titre.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        usernameLabel.setForeground(java.awt.Color.WHITE);
        usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JTextField usernameField = new JTextField();
        usernameField.setMaximumSize(new Dimension(250, 30));

        JLabel passLabel = new JLabel("Mot de passe :");
        passLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        passLabel.setForeground(java.awt.Color.WHITE);
        passLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPasswordField passField = new JPasswordField();
        passField.setMaximumSize(new Dimension(250, 30));

        JButton registerButton = new JButton("S'INSCRIRE");
        registerButton.setFont(new Font("Monospaced", Font.BOLD, 18));
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // --- LIEN POUR RETOURNER À LA CONNEXION ---
        JLabel retourConnexionLabel = new JLabel("<html><a href=\"\">Retour à la connexion</a></html>");
        retourConnexionLabel.setFont(new Font("Monospaced", Font.PLAIN, 14));
        retourConnexionLabel.setCursor(new Cursor(Cursor.HAND_CURSOR)); 
        retourConnexionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        retourConnexionLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        
        retourConnexionLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                afficherEcranConnexion(); // Retour à l'écran de connexion
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText().trim();
                String password = new String(passField.getPassword());

                if (inscrireUtilisateur(username, password)) {
                    JOptionPane.showMessageDialog(jFrame, "Compte créé avec succès ! Vous pouvez maintenant vous connecter.", "Succès", JOptionPane.INFORMATION_MESSAGE);
                    afficherEcranConnexion(); // Retour à la connexion après succès
                } else {
                    JOptionPane.showMessageDialog(jFrame, "Erreur lors de la création du compte. Vérifiez que le serveur est lancé.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Ajout des éléments avec des espaces
        backgroundLabel.add(Box.createRigidArea(new Dimension(0, 50)));
        backgroundLabel.add(titre);
        backgroundLabel.add(Box.createRigidArea(new Dimension(0, 30)));
        backgroundLabel.add(usernameLabel);
        backgroundLabel.add(usernameField);
        backgroundLabel.add(Box.createRigidArea(new Dimension(0, 15)));
        backgroundLabel.add(passLabel);
        backgroundLabel.add(passField);
        backgroundLabel.add(Box.createRigidArea(new Dimension(0, 30)));
        backgroundLabel.add(registerButton);
        backgroundLabel.add(Box.createRigidArea(new Dimension(0, 15))); 
        backgroundLabel.add(retourConnexionLabel);

        // Rafraîchissement de la fenêtre
        backgroundLabel.revalidate();
        backgroundLabel.repaint();
    }
    
 // --- NOUVELLE MÉTHODE : REQUÊTE HTTP CRÉATION DE COMPTE ---
    private boolean inscrireUtilisateur(String username, String password) {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            return false;
        }

        HttpURLConnection connection = null;
        try {
            // URL de votre API d'inscription
            URL url = new URL("http://localhost:8080/test/api/users");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            // Construction du corps JSON
            String requestBody = "{\"username\":\"" + escapeJson(username) + "\",\"password\":\"" + escapeJson(password) + "\"}";
            
            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(requestBody.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = connection.getResponseCode();

            // Si le code de réponse est 200 (OK) ou 201 (Created)
            if (responseCode >= 200 && responseCode < 300) {
                return true;
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return false;
    }
    

    // Méthode de vérification
    private boolean verifierIdentifiants(String username, String password) {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            return false;
        }

        HttpURLConnection connection = null;	
        try {
            URL url = new URL(LOGIN_API_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            String requestBody = "{\"username\":\"" + escapeJson(username) + "\",\"password\":\"" + escapeJson(password) + "\"}";
            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(requestBody.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = connection.getResponseCode();
            String responseBody = readResponseBody(responseCode >= 200 && responseCode < 300
                ? connection.getInputStream()
                : connection.getErrorStream());

            if (responseCode >= 200 && responseCode < 300 && responseBody.contains("\"ok\":true")) {
                sessionCookie = extractSessionCookie(connection.getHeaderFields());
                usernameConnecte = extractJsonValue(responseBody, "username");
                return true;
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return false;
    }

    private String readResponseBody(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return "";
        }

        StringBuilder responseBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                responseBuilder.append(line);
            }
        }
        return responseBuilder.toString();
    }

    private String extractSessionCookie(Map<String, List<String>> headers) {
        List<String> setCookieHeaders = headers.get("Set-Cookie");
        if (setCookieHeaders == null) {
            return null;
        }

        for (String header : setCookieHeaders) {
            if (header.startsWith("JSESSIONID=")) {
                int endIndex = header.indexOf(';');
                return endIndex >= 0 ? header.substring(0, endIndex) : header;
            }
        }

        return null;
    }

    private String extractJsonValue(String json, String key) {
        Pattern pattern = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*\"([^\"]*)\"");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    // --- MÉTHODE ADAPTÉE : AFFICHER LE MENU PRINCIPAL ---
    public void afficherMenuPrincipal() {
        backgroundLabel.removeAll(); // Nettoie l'écran de connexion

        backgroundLabel.add(Box.createRigidArea(new Dimension(0, 20))); // Marge en haut

        //-------------LABEL DE TITRE-------------
        JLabel titre = new JLabel("PACMAN");
        titre.setFont(new Font("Monospaced", Font.BOLD, 20));
        titre.setForeground(java.awt.Color.WHITE);
        titre.setAlignmentX(Component.CENTER_ALIGNMENT);
        backgroundLabel.add(titre);
        backgroundLabel.add(Box.createRigidArea(new Dimension(0, 20)));

        //-------------CHOIX DU NIVEAU-------------
        JLabel choixNiveauLabel = new JLabel("Choisir niveau :");
        choixNiveauLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        choixNiveauLabel.setForeground(java.awt.Color.WHITE);
        choixNiveauLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        backgroundLabel.add(choixNiveauLabel);
        backgroundLabel.add(Box.createRigidArea(new Dimension(0, 10)));

        choixNiveau = new JComboBox<>();
        choixNiveau.setFont(new Font("Monospaced", Font.PLAIN, 14));
        choixNiveau.setMaximumSize(new Dimension(300, 40));
        chargerNiveaux();
        backgroundLabel.add(choixNiveau);
        backgroundLabel.add(Box.createRigidArea(new Dimension(0, 10)));

        //-------------CHOIX DE LA DIFFICULTE-------------
        JLabel diffculteLabel = new JLabel("Difficulté : ");
        diffculteLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        diffculteLabel.setForeground(java.awt.Color.WHITE);
        diffculteLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        backgroundLabel.add(diffculteLabel);

        String[] difficultes = {"Decouverte", "Facile", "Difficile", "Extreme"};
        choixDifficulte = new JComboBox<>(difficultes);
        choixDifficulte.setFont(new Font("Monospaced", Font.BOLD, 14));
        choixDifficulte.setMaximumSize(new Dimension(300, 40));
        choixDifficulte.setSelectedIndex(1);
        backgroundLabel.add(choixDifficulte);
        
        //-------------CHOIX DU MODE DE JEU-------------
        JPanel modePanel = new JPanel();
        modePanel.setLayout(new BoxLayout(modePanel, BoxLayout.X_AXIS));
        modePanel.setOpaque(false);
        
        JRadioButton radioSolo = new JRadioButton("Solo");
        radioSolo.setSelected(true);
        radioSolo.setOpaque(false);
        radioSolo.setForeground(java.awt.Color.WHITE);
        radioSolo.setFont(new Font("Monospaced", Font.BOLD, 14));

        JRadioButton radioMulti = new JRadioButton("Multijoueur");
        radioMulti.setOpaque(false);
        radioMulti.setForeground(java.awt.Color.WHITE);
        radioMulti.setFont(new Font("Monospaced", Font.BOLD, 14));

        ButtonGroup groupeMode = new ButtonGroup();
        groupeMode.add(radioSolo);
        groupeMode.add(radioMulti);

        JButton creerRoomBtn = new JButton("Creer une room");
        creerRoomBtn.setFont(new Font("Monospaced", Font.BOLD, 12));
        creerRoomBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        creerRoomBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionCreerRoom(); 
            }
        });

        roomIdField = new JTextField();
        roomIdField.setMaximumSize(new Dimension(80, 25));
        roomIdField.setFont(new Font("Monospaced", Font.PLAIN, 12));
        roomIdField.setToolTipText("ID Room");

        JButton rejoindreRoomBtn = new JButton("Rejoindre");
        rejoindreRoomBtn.setFont(new Font("Monospaced", Font.BOLD, 12));
        rejoindreRoomBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        rejoindreRoomBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionRejoindreRoom();
            }
        });

        modePanel.add(radioSolo);
        modePanel.add(Box.createRigidArea(new Dimension(10, 0)));
        modePanel.add(radioMulti);
        modePanel.add(Box.createRigidArea(new Dimension(30, 0)));
        modePanel.add(creerRoomBtn);
        modePanel.add(Box.createRigidArea(new Dimension(10, 0)));
        modePanel.add(roomIdField);
        modePanel.add(Box.createRigidArea(new Dimension(5, 0)));
        modePanel.add(rejoindreRoomBtn);

        backgroundLabel.add(modePanel);
        backgroundLabel.add(Box.createRigidArea(new Dimension(0, 20)));

        //-------------BOUTON COMMENCER-------------
        JButton startButton = new JButton("COMMENCER");
        startButton.setFont(new Font("Monospaced", Font.BOLD, 24));
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backgroundLabel.add(startButton);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    lancerJeu();
                    jFrame.dispose();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
        

        
        //-------------BOUTON VOIR STATS / EDITER-------------
        JPanel voirStatsEditer = new JPanel();
        voirStatsEditer.setLayout(new BoxLayout(voirStatsEditer, BoxLayout.X_AXIS));
        voirStatsEditer.setOpaque(false);

        JButton editerBouton = new JButton("CREER NIVEAU");
        editerBouton.setFont(new Font("Monospaced", Font.BOLD, 24));
        editerBouton.setAlignmentX(Component.CENTER_ALIGNMENT);
        editerBouton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton voirStatsBouton = new JButton("VOIR STATS");
        voirStatsBouton.setFont(new Font("Monospaced", Font.BOLD, 24));
        voirStatsBouton.setAlignmentX(Component.CENTER_ALIGNMENT);
        voirStatsBouton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        editerBouton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new LevelEditor();
            }
        });

        voirStatsBouton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new StatistiquesWindow();
            }
        });

        voirStatsEditer.add(voirStatsBouton);
        voirStatsEditer.add(Box.createRigidArea(new Dimension(10, 0)));
        voirStatsEditer.add(editerBouton);

        backgroundLabel.add(voirStatsEditer);

        jFrame.pack();
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);

        // Indispensable pour demander à Java de redessiner les nouveaux boutons
        backgroundLabel.revalidate();
        backgroundLabel.repaint();
    }

    private ImageIcon loadImageIcon(String resourcePath, String... fallbackPaths) {
        URL imageUrl = getClass().getResource(resourcePath);
        if (imageUrl != null) {
            return new ImageIcon(imageUrl);
        }

        for (String fallbackPath : fallbackPaths) {
            File imageFile = new File(fallbackPath);
            if (imageFile.exists()) {
                return new ImageIcon(fallbackPath);
            }
        }

        return new ImageIcon();
    }

    public void launchMusic() {
        File musicPath = new File("music/audio.wav");
        AudioInputStream audioInputStream;
        try {
            if (clip != null && clip.isRunning()) return;
            audioInputStream = AudioSystem.getAudioInputStream(musicPath);
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void chargerNiveaux() {
        File folder = new File("layouts");
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".lay"));
            if (files.length > 0) {
                for (File file : files) {
                    choixNiveau.addItem(file.getName());
                }
            }
        }
    }

    protected void actionCreerRoom() {
        String ipServeur = JOptionPane.showInputDialog(jFrame, "Entrez l'adresse IP du serveur :", "localhost");
        if (ipServeur == null || ipServeur.trim().isEmpty()) {
            return;
        }

        String roomId = java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        int difficulte = choixDifficulte.getSelectedIndex();
        double diff = getValeurDifficulte(difficulte);
        String niveau = "src/layouts/" + choixNiveau.getSelectedItem();

        try {
            PacmanGame fakeGame = new PacmanGame(1000, niveau, diff);
            ViewPacmanGame viewGame = new ViewPacmanGame(fakeGame.getMaze());
            new GameClient(ipServeur, 9081, viewGame, niveau, diff, roomId, true, false, sessionCookie);
            new RoomWindow(roomId);
            jFrame.dispose(); 
            
        } catch (Exception e) {
        }
    }

    protected void actionRejoindreRoom() {
        String roomId = roomIdField.getText().trim();

        String ipServeur = JOptionPane.showInputDialog(jFrame, "Entrez l'adresse IP du serveur :", "localhost");
        if (ipServeur == null || ipServeur.trim().isEmpty()) {
            return; 
        }

        try {
            new GameClient(ipServeur, 9081, null, "", 0.0, roomId, false, false, sessionCookie);
            jFrame.dispose();
            
        } catch (Exception e) {
        }
    }

    public void lancerJeu() throws Exception {
        String choixFichier = (String) choixNiveau.getSelectedItem();
        String path = "src/layouts/" + choixFichier;

        int difficulte = choixDifficulte.getSelectedIndex();
        double diff = getValeurDifficulte(difficulte);
        
        new ControllerPacmanGame(path, diff);
    }

    protected double getValeurDifficulte(int index) {
        if (index == 0) {
        	return 0.1;
        }
        else if (index == 2) {
        	return 0.7;
        }
        else if (index == 3) {
        	return 0.9;
        }
        return 0.4;
    }
}