package vue;

import java.awt.Desktop;
import java.net.URI;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.awt.BorderLayout;
import java.awt.Color;
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
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.JTextField;

import clientMain.GameClient;
import controller.ControllerPacmanGame;
import game.PacmanGame;

public class GameLauncher {

    protected JComboBox<String> choixNiveau;
    protected JComboBox<String> choixDifficulte;
    protected JTextField roomIdField;
    public static Clip clip;

    private static final boolean USE_ONLINE_SERVER = false;
    private static final String WEB_BASE_URL = USE_ONLINE_SERVER
            ? "http://46.101.67.203:8080/tomcat"
            : "http://localhost:8080/test";
    private static final String LOGIN_API_URL = WEB_BASE_URL + "/api/auth/login";
    private static final String REGISTER_PAGE_URL = WEB_BASE_URL + "/api/users";
    private static final String BRIDGE_TOKEN_API_URL = WEB_BASE_URL + "/api/auth/bridge-token";
    private static final String BRIDGE_LOGIN_URL = WEB_BASE_URL + "/bridge-login";
    private static final String SCORE_HISTORY_API_URL = WEB_BASE_URL + "/api/scores/history?limit=5&offset=0";
    private static final String LEADERBOARD_API_URL = WEB_BASE_URL + "/api/leaderboard";
    private static final String HISTORY_PAGE_URL = WEB_BASE_URL + "/history.jsp";
    private static final String LEADERBOARD_PAGE_URL = WEB_BASE_URL + "/leaderboard.jsp";

    // On rend la fenêtre et le fond accessibles aux autres méthodes
    // http://localhost:8080/test/TestDBServlet
    protected JFrame jFrame;
    protected JLabel backgroundLabel;
    protected String sessionCookie;
    protected String usernameConnecte;

    public GameLauncher() {
        jFrame = new JFrame();
        jFrame.setTitle("Pacman");

        int largeurFenetre = 800;
        int hauteurFenetre = 600;

        // -------------IMAGE BACKGROUND---------------
        ImageIcon image = loadImageIcon("/image/pacmanImage.jpg", "src/image/pacmanImage.jpg", "image/pacmanImage.jpg");
        if (image.getIconWidth() != -1) {
            java.awt.Image img = image.getImage();
            java.awt.Image newImg = img.getScaledInstance(largeurFenetre, hauteurFenetre, java.awt.Image.SCALE_SMOOTH);
            backgroundLabel = new JLabel(new ImageIcon(newImg));
        } else {
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

    protected String getScoreApiUrl() {
        return WEB_BASE_URL + "/api/scores";
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

        // --- NOUVEAU : LIEN "CRÉER UN COMPTE" ---// On ajoute également style=\"color:
        // white;\" ici
        JLabel creerCompteLabel = new JLabel("<html><a href=\"\" style=\"color: white;\">Créer un compte</a></html>");
        creerCompteLabel.setFont(new Font("Monospaced", Font.PLAIN, 14));
        creerCompteLabel.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Curseur "main"
        creerCompteLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        creerCompteLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        // Ajout de l'événement au clic
        creerCompteLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ouvrirPageWeb(REGISTER_PAGE_URL);
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
                    JOptionPane.showMessageDialog(jFrame,
                            "Connexion echouee. Verifiez le username, le mot de passe, ou le serveur web.", "Erreur",
                            JOptionPane.ERROR_MESSAGE);
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
        JLabel retourConnexionLabel = new JLabel(
                "<html><a href=\"\" style=\"color: white;\">Retour à la connexion</a></html>");
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
                    JOptionPane.showMessageDialog(jFrame,
                            "Compte créé avec succès ! Vous pouvez maintenant vous connecter.", "Succès",
                            JOptionPane.INFORMATION_MESSAGE);
                    afficherEcranConnexion(); // Retour à la connexion après succès
                } else {
                    JOptionPane.showMessageDialog(jFrame,
                            "Erreur lors de la création du compte. Vérifiez que le serveur est lancé.", "Erreur",
                            JOptionPane.ERROR_MESSAGE);
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
            URL url = new URL(REGISTER_PAGE_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(15000);

            // Construction du corps JSON
            String requestBody = "{\"username\":\"" + escapeJson(username) + "\",\"password\":\"" + escapeJson(password)
                    + "\"}";

            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(requestBody.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = connection.getResponseCode();

            // Si le code de réponse est 200 (OK) ou 201 (Created)
            if (responseCode >= 200 && responseCode < 300) {
                return true;
            }
        } catch (IOException exception) {
            System.out.println("[LOGIN] Exception : " + exception.getMessage());
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
            System.out.println("[LOGIN] Appel API : " + LOGIN_API_URL + " username=" + username);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(15000);

            String requestBody = "{\"username\":\"" + escapeJson(username) + "\",\"password\":\"" + escapeJson(password)
                    + "\"}";
            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(requestBody.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = connection.getResponseCode();
            String responseBody = readResponseBody(responseCode >= 200 && responseCode < 300
                    ? connection.getInputStream()
                    : connection.getErrorStream());
            System.out.println("[LOGIN] HTTP " + responseCode + " -> " + responseBody);

            if (responseCode >= 200 && responseCode < 300 && responseBody.contains("\"ok\":true")) {
                sessionCookie = extractSessionCookie(connection.getHeaderFields());
                usernameConnecte = extractJsonValue(responseBody, "username");
                System.out.println("[LOGIN] Succes pour user=" + usernameConnecte + " cookie=" + sessionCookie);
                return true;
            }

            System.out.println("[LOGIN] Echec authentification pour username=" + username);
        } catch (IOException exception) {
            System.out.println("[LOGIN] Exception : " + exception.getMessage());
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

    private void ouvrirPageWebAvecBridge(String target) {
        String token = demanderBridgeToken();
        if (token == null || token.isEmpty()) {
            JOptionPane.showMessageDialog(
                    jFrame,
                    "Impossible d'ouvrir la page web automatiquement. Reconnectez-vous puis reessayez.",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String url = BRIDGE_LOGIN_URL
                + "?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8)
                + "&target=" + URLEncoder.encode(target, StandardCharsets.UTF_8);
        ouvrirPageWeb(url);
    }

    private String demanderBridgeToken() {
        if (sessionCookie == null || sessionCookie.isEmpty()) {
            return null;
        }

        HttpURLConnection connection = null;
        try {
            URL url = new URL(BRIDGE_TOKEN_API_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(15000);
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Cookie", sessionCookie);

            int responseCode = connection.getResponseCode();
            String responseBody = readResponseBody(responseCode >= 200 && responseCode < 300
                    ? connection.getInputStream()
                    : connection.getErrorStream());

            if (responseCode >= 200 && responseCode < 300) {
                return extractJsonValue(responseBody, "token");
            }
        } catch (IOException exception) {
            System.out.println("[BRIDGE] Exception : " + exception.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return null;
    }

    private void ouvrirPageWeb(String url) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
                return;
            }
        } catch (Exception exception) {
            // Fallback below
        }

        try {
            if (ouvrirPageWebViaCommande(url)) {
                return;
            }
        } catch (Exception exception) {
            // Show final error below
        }

        JOptionPane.showMessageDialog(
                jFrame,
                "Impossible d'ouvrir automatiquement le navigateur.\nOuvrez manuellement : " + url,
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
    }

    private boolean ouvrirPageWebViaCommande(String url) throws IOException {
        String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);

        if (os.contains("win")) {
            Runtime.getRuntime().exec(new String[] { "rundll32", "url.dll,FileProtocolHandler", url });
            return true;
        }

        if (os.contains("mac")) {
            Runtime.getRuntime().exec(new String[] { "open", url });
            return true;
        }

        Runtime.getRuntime().exec(new String[] { "xdg-open", url });
        return true;
    }

    // --- MÉTHODE ADAPTÉE : AFFICHER LE MENU PRINCIPAL ---
    public void afficherMenuPrincipal() {
        backgroundLabel.removeAll(); // Nettoie l'écran de connexion

        backgroundLabel.add(Box.createRigidArea(new Dimension(0, 20))); // Marge en haut

        // -------------LABEL DE TITRE-------------
        JLabel titre = new JLabel("PACMAN");
        titre.setFont(new Font("Monospaced", Font.BOLD, 20));
        titre.setForeground(java.awt.Color.WHITE);
        titre.setAlignmentX(Component.CENTER_ALIGNMENT);
        backgroundLabel.add(titre);
        backgroundLabel.add(Box.createRigidArea(new Dimension(0, 20)));

        // -------------CHOIX DU NIVEAU-------------
        JLabel choixNiveauLabel = new JLabel("Choisir niveau :");
        choixNiveauLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        choixNiveauLabel.setForeground(java.awt.Color.WHITE);
        choixNiveauLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        backgroundLabel.add(choixNiveauLabel);
        backgroundLabel.add(Box.createRigidArea(new Dimension(0, 10)));

        choixNiveau = new JComboBox<>();
        choixNiveau.setFont(new Font("Monospaced", Font.PLAIN, 14));
        choixNiveau.setMaximumSize(new Dimension(300, 40));
        backgroundLabel.add(choixNiveau);
        backgroundLabel.add(Box.createRigidArea(new Dimension(0, 10)));

        // -------------CHOIX DE LA DIFFICULTE-------------
        JLabel diffculteLabel = new JLabel("Difficulté : ");
        diffculteLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        diffculteLabel.setForeground(java.awt.Color.WHITE);
        diffculteLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        backgroundLabel.add(diffculteLabel);

        String[] difficultes = { "Decouverte", "Facile", "Difficile", "Extreme" };
        choixDifficulte = new JComboBox<>(difficultes);
        choixDifficulte.setFont(new Font("Monospaced", Font.BOLD, 14));
        choixDifficulte.setMaximumSize(new Dimension(300, 40));
        choixDifficulte.setSelectedIndex(1);
        backgroundLabel.add(choixDifficulte);

        // -------------CHOIX DU MODE DE JEU-------------
        JPanel modePanel = new JPanel();
        modePanel.setLayout(new BoxLayout(modePanel, BoxLayout.X_AXIS));
        modePanel.setOpaque(false);

        JRadioButton radioSolo = new JRadioButton("Solo");
        radioSolo.setSelected(true);
        chargerNiveaux(false, false);
        // on charge les niveaux solos la premiere fois
        radioSolo.setOpaque(false);
        radioSolo.setForeground(java.awt.Color.WHITE);
        radioSolo.setFont(new Font("Monospaced", Font.BOLD, 14));

        JRadioButton radioMulti = new JRadioButton("Multijoueur");
        radioMulti.setOpaque(false);
        radioMulti.setForeground(java.awt.Color.WHITE);
        radioMulti.setFont(new Font("Monospaced", Font.BOLD, 14));

        JRadioButton radioPersonnalise = new JRadioButton("Personnalisé");
        radioPersonnalise.setOpaque(false);
        radioPersonnalise.setForeground(java.awt.Color.WHITE);
        radioPersonnalise.setFont(new Font("Monospaced", Font.BOLD, 14));

        ButtonGroup groupeMode = new ButtonGroup();
        groupeMode.add(radioSolo);
        groupeMode.add(radioMulti);
        groupeMode.add(radioPersonnalise);

        modePanel.add(radioSolo);
        modePanel.add(radioMulti);
        modePanel.add(radioPersonnalise);
        backgroundLabel.add(modePanel);

        // ----------PARTIE MULTIJOUEUR-----------

        JPanel multiplayerPanel = new JPanel();
        multiplayerPanel.setLayout(new BoxLayout(multiplayerPanel, BoxLayout.X_AXIS));
        multiplayerPanel.setOpaque(false);
        multiplayerPanel.setVisible(false);

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

        multiplayerPanel.add(creerRoomBtn);
        multiplayerPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        multiplayerPanel.add(roomIdField);
        multiplayerPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        multiplayerPanel.add(rejoindreRoomBtn);

        backgroundLabel.add(multiplayerPanel);

        ActionListener toggleMultiplayer = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                multiplayerPanel.setVisible(radioMulti.isSelected());
                chargerNiveaux(radioMulti.isSelected(), radioPersonnalise.isSelected());

                backgroundLabel.revalidate();
                backgroundLabel.repaint();
            }
        };

        radioSolo.addActionListener(toggleMultiplayer);
        radioMulti.addActionListener(toggleMultiplayer);
        radioPersonnalise.addActionListener(toggleMultiplayer);

        // -------------BOUTON COMMENCER-------------
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

        // -------------BOUTON VOIR STATS / EDITER-------------
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

        JButton leaderboardBouton = new JButton("LEADERBOARD");
        leaderboardBouton.setFont(new Font("Monospaced", Font.BOLD, 24));
        leaderboardBouton.setAlignmentX(Component.CENTER_ALIGNMENT);
        leaderboardBouton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        editerBouton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new LevelEditor();
            }
        });

        voirStatsBouton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ouvrirPageWebAvecBridge("history");
            }
        });

        leaderboardBouton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ouvrirPageWebAvecBridge("leaderboard");
            }
        });

        voirStatsEditer.add(voirStatsBouton);
        voirStatsEditer.add(Box.createRigidArea(new Dimension(10, 0)));
        voirStatsEditer.add(leaderboardBouton);
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

    // --- NOUVELLE MÉTHODE : AFFICHER L'ÉCRAN DES STATISTIQUES ---
    public void afficherEcranStatistiques() {
        backgroundLabel.removeAll();
        backgroundLabel.add(Box.createRigidArea(new Dimension(0, 25)));

        String nomAffiche = (usernameConnecte != null && !usernameConnecte.isEmpty()) ? usernameConnecte
                : "Joueur Inconnu";
        JLabel titre = new JLabel("Historique de " + nomAffiche);
        titre.setFont(new Font("Monospaced", Font.BOLD, 28));
        titre.setForeground(Color.WHITE);
        titre.setAlignmentX(Component.CENTER_ALIGNMENT);
        backgroundLabel.add(titre);
        backgroundLabel.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel statsPanel = new JPanel(new BorderLayout());
        statsPanel.setOpaque(true);
        statsPanel.setBackground(new Color(10, 18, 45, 205));
        statsPanel.setMaximumSize(new Dimension(640, 320));
        statsPanel.setPreferredSize(new Dimension(640, 320));
        statsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel statsLabel = createLoadingLabel("Chargement de l'historique...");
        JScrollPane scrollPane = createContentScrollPane(statsLabel);

        statsPanel.add(scrollPane, BorderLayout.CENTER);
        backgroundLabel.add(statsPanel);
        backgroundLabel.add(Box.createRigidArea(new Dimension(0, 25)));

        JButton retourBouton = new JButton("RETOUR AU MENU");
        retourBouton.setFont(new Font("Monospaced", Font.BOLD, 18));
        retourBouton.setAlignmentX(Component.CENTER_ALIGNMENT);
        retourBouton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        retourBouton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                afficherMenuPrincipal();
            }
        });

        backgroundLabel.add(retourBouton);
        backgroundLabel.revalidate();
        backgroundLabel.repaint();

        chargerHtmlAsync(statsLabel, new HtmlSupplier() {
            @Override
            public String get() {
                return recupererStatsDuServeur(sessionCookie);
            }
        });
    }

    private JLabel createLoadingLabel(String message) {
        JLabel label = new JLabel("<html><div style='text-align:center; color:white; padding-top:110px;'><b>" + message
                + "</b></div></html>");
        label.setFont(new Font("Monospaced", Font.PLAIN, 15));
        label.setForeground(Color.WHITE);
        label.setVerticalAlignment(JLabel.TOP);
        return label;
    }

    private JScrollPane createContentScrollPane(JLabel contentLabel) {
        JScrollPane scrollPane = new JScrollPane(contentLabel);
        scrollPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(18, 18, 18, 18));
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        return scrollPane;
    }

    private void chargerHtmlAsync(JLabel targetLabel, HtmlSupplier supplier) {
        Thread loader = new Thread(() -> {
            String html;
            try {
                html = supplier.get();
            } catch (Exception exception) {
                html = "<div style='text-align:center;'>Erreur inattendue : " + exception.getMessage() + "</div>";
            }

            final String finalHtml = html;
            SwingUtilities.invokeLater(() -> {
                targetLabel.setText("<html>" + finalHtml + "</html>");
                targetLabel.revalidate();
                targetLabel.repaint();
            });
        });
        loader.setDaemon(true);
        loader.start();
    }

    private interface HtmlSupplier {
        String get();
    }

    // --- NOUVELLE MÉTHODE : RÉCUPÉRER LES STATS ---
    private String recupererStatsDuServeur(String sessionCookie) {
        if (sessionCookie == null || sessionCookie.isEmpty()) {
            return "<div style='text-align:center;'>Erreur : vous n'etes pas connecte.</div>";
        }

        try {
            String statsUrl = SCORE_HISTORY_API_URL;
            System.out.println("[STATS] Appel API : " + statsUrl);
            URL url = new URL(statsUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(15000);
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
                System.out.println("[STATS] HTTP " + responseCode + " -> " + response);
                return formatterHistoriqueScores(response.toString());
            } else if (responseCode == 401 || responseCode == 403) {
                System.out.println("[STATS] HTTP " + responseCode + " -> acces refuse");
                return "<div style='text-align:center;'>Acces refuse. Votre session a peut-etre expire. (Erreur "
                        + responseCode + ")</div>";
            } else {
                System.out.println("[STATS] HTTP " + responseCode + " -> erreur serveur");
                return "<div style='text-align:center;'>Impossible de recuperer l'historique. (Code erreur : "
                        + responseCode + ")</div>";
            }
        } catch (Exception e) {
            System.out.println("[STATS] Exception : " + e.getMessage());
            e.printStackTrace();
            return "<div style='text-align:center;'>Erreur de connexion au serveur distant.</div>";
        }
    }

    private String formatterHistoriqueScores(String json) {
        String bestScore = extractNumericJsonValue(json, "bestScore");
        String totalScores = extractNumericJsonValue(json, "totalScores");

        Pattern itemPattern = Pattern
                .compile("\\{\\s*\"score\"\\s*:\\s*(\\d+)\\s*,\\s*\"createdAt\"\\s*:\\s*\"([^\"]+)\"\\s*\\}");
        Matcher matcher = itemPattern.matcher(json);

        StringBuilder html = new StringBuilder();
        html.append("<div style='color:white; font-family:monospace;'>");
        html.append("<div style='text-align:center; font-size:16px; margin-bottom:16px;'>");
        html.append("<b>Meilleur score :</b> ").append(bestScore != null ? bestScore : "0")
                .append("&nbsp;&nbsp;&nbsp;");
        html.append("<b>Parties :</b> ").append(totalScores != null ? totalScores : "0");
        html.append("</div>");
        html.append("<table style='width:100%; border-collapse:collapse; color:white;'>");
        html.append("<tr>");
        html.append("<th style='border-bottom:1px solid #6ee7ff; padding:8px; text-align:left;'>#</th>");
        html.append("<th style='border-bottom:1px solid #6ee7ff; padding:8px; text-align:left;'>Score</th>");
        html.append("<th style='border-bottom:1px solid #6ee7ff; padding:8px; text-align:left;'>Date</th>");
        html.append("</tr>");

        int index = 1;
        while (matcher.find()) {
            html.append("<tr>");
            html.append("<td style='padding:8px; border-bottom:1px solid rgba(255,255,255,0.18);'>").append(index++)
                    .append("</td>");
            html.append("<td style='padding:8px; border-bottom:1px solid rgba(255,255,255,0.18);'><b>")
                    .append(matcher.group(1)).append("</b></td>");
            html.append("<td style='padding:8px; border-bottom:1px solid rgba(255,255,255,0.18);'>")
                    .append(formatterDateHumaine(matcher.group(2))).append("</td>");
            html.append("</tr>");
        }

        if (index == 1) {
            html.append("<tr><td colspan='3' style='padding:12px; text-align:center;'>Aucun score trouve.</td></tr>");
        }

        html.append("</table>");
        html.append("</div>");
        return html.toString();
    }

    private String formatterDateHumaine(String dateTexte) {
        DateTimeFormatter[] inputFormats = new DateTimeFormatter[] {
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        };

        for (DateTimeFormatter inputFormat : inputFormats) {
            try {
                LocalDateTime date = LocalDateTime.parse(dateTexte, inputFormat);
                return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'a' HH:mm"));
            } catch (DateTimeParseException exception) {
            }
        }

        return dateTexte;
    }

    private String extractNumericJsonValue(String json, String key) {
        Pattern pattern = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*(\\d+)");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    // --- NOUVELLE MÉTHODE : AFFICHER LE LEADERBOARD ---
    public void afficherEcranLeaderboard() {
        backgroundLabel.removeAll();
        backgroundLabel.add(Box.createRigidArea(new Dimension(0, 25)));

        JLabel titre = new JLabel("CLASSEMENT PUBLIC");
        titre.setFont(new Font("Monospaced", Font.BOLD, 28));
        titre.setForeground(Color.WHITE);
        titre.setAlignmentX(Component.CENTER_ALIGNMENT);
        backgroundLabel.add(titre);
        backgroundLabel.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel leaderboardPanel = new JPanel(new BorderLayout());
        leaderboardPanel.setOpaque(true);
        leaderboardPanel.setBackground(new Color(10, 18, 45, 205));
        leaderboardPanel.setMaximumSize(new Dimension(640, 320));
        leaderboardPanel.setPreferredSize(new Dimension(640, 320));
        leaderboardPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel leaderboardLabel = createLoadingLabel("Chargement du classement...");
        JScrollPane scrollPane = createContentScrollPane(leaderboardLabel);

        leaderboardPanel.add(scrollPane, BorderLayout.CENTER);
        backgroundLabel.add(leaderboardPanel);
        backgroundLabel.add(Box.createRigidArea(new Dimension(0, 25)));

        JButton retourBouton = new JButton("RETOUR AU MENU");
        retourBouton.setFont(new Font("Monospaced", Font.BOLD, 18));
        retourBouton.setAlignmentX(Component.CENTER_ALIGNMENT);
        retourBouton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        retourBouton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                afficherMenuPrincipal();
            }
        });

        backgroundLabel.add(retourBouton);
        backgroundLabel.revalidate();
        backgroundLabel.repaint();

        chargerHtmlAsync(leaderboardLabel, new HtmlSupplier() {
            @Override
            public String get() {
                return recupererLeaderboardDuServeur();
            }
        });
    }

    // --- NOUVELLE MÉTHODE : RÉCUPÉRER LE LEADERBOARD ---
    private String recupererLeaderboardDuServeur() {
        try {
            String leaderboardUrl = LEADERBOARD_API_URL;
            System.out.println("[LEADERBOARD] Appel API : " + leaderboardUrl);
            URL url = new URL(leaderboardUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(15000);

            int responseCode = connection.getResponseCode();
            if (responseCode >= 200 && responseCode < 300) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                System.out.println("[LEADERBOARD] HTTP " + responseCode + " -> " + response);
                return formatterLeaderboard(response.toString());
            }
            System.out.println("[LEADERBOARD] HTTP " + responseCode + " -> erreur serveur");
            return "<div style='text-align:center;'>Impossible de recuperer le classement. (Code erreur : "
                    + responseCode + ")</div>";
        } catch (Exception e) {
            System.out.println("[LEADERBOARD] Exception : " + e.getMessage());
            e.printStackTrace();
            return "<div style='text-align:center;'>Erreur de connexion au serveur distant.</div>";
        }
    }

    private String formatterLeaderboard(String json) {
        String totalUsers = extractNumericJsonValue(json, "totalUsers");
        String count = extractNumericJsonValue(json, "count");
        Pattern itemPattern = Pattern.compile(
                "\\{\\s*\"rank\"\\s*:\\s*(\\d+)\\s*,\\s*\"username\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*\"bestScore\"\\s*:\\s*(\\d+)\\s*\\}");
        Matcher matcher = itemPattern.matcher(json);

        StringBuilder html = new StringBuilder();
        html.append("<div style='color:white; font-family:monospace;'>");
        html.append("<div style='text-align:center; font-size:16px; margin-bottom:16px;'>");
        html.append("<b>Joueurs classes :</b> ").append(count != null ? count : "0").append("&nbsp;&nbsp;&nbsp;");
        html.append("<b>Total joueurs :</b> ").append(totalUsers != null ? totalUsers : "0");
        html.append("</div>");
        html.append("<table style='width:100%; border-collapse:collapse; color:white;'>");
        html.append("<tr>");
        html.append("<th style='border-bottom:1px solid #6ee7ff; padding:8px; text-align:left;'>Rang</th>");
        html.append("<th style='border-bottom:1px solid #6ee7ff; padding:8px; text-align:left;'>Joueur</th>");
        html.append("<th style='border-bottom:1px solid #6ee7ff; padding:8px; text-align:left;'>Meilleur score</th>");
        html.append("</tr>");

        boolean hasData = false;
        while (matcher.find()) {
            hasData = true;
            html.append("<tr>");
            html.append("<td style='padding:8px; border-bottom:1px solid rgba(255,255,255,0.18);'><b>#")
                    .append(matcher.group(1)).append("</b></td>");
            html.append("<td style='padding:8px; border-bottom:1px solid rgba(255,255,255,0.18);'>")
                    .append(matcher.group(2)).append("</td>");
            html.append("<td style='padding:8px; border-bottom:1px solid rgba(255,255,255,0.18);'>")
                    .append(matcher.group(3)).append("</td>");
            html.append("</tr>");
        }

        if (!hasData) {
            html.append(
                    "<tr><td colspan='3' style='padding:12px; text-align:center;'>Aucun joueur classe pour le moment.</td></tr>");
        }

        html.append("</table>");
        html.append("</div>");
        return html.toString();
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
        File musicPath = new File("src/music/audio.wav");
        AudioInputStream audioInputStream;
        try {
            if (clip != null && clip.isRunning())
                return;
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

    public void chargerNiveaux(boolean multi, boolean personnalise) {
        ArrayList<String> listeNiveauxSolo = new ArrayList<String>(List.of(
                "Solo_1.lay",
                "Solo_2.lay",
                "Solo_3.lay",
                "Solo_4.lay"));
        ArrayList<String> listeNiveauxMultis = new ArrayList<String>(List.of(
                "Duo_1.lay",
                "Duo_2.lay",
                "Duo_3.lay"));
        choixNiveau.removeAllItems();
        File folder = new File("src/layouts");
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".lay"));
            if (files.length > 0) {
                /*
                 * En fonction du radio button selectionne :
                 * soit c'est en mode solo, mode multi, ou mode personnalise
                 * pour l'instant on peut pas faire de personnalise en multi
                 */
                if (personnalise) {
                    for (File file : files) {
                        if (!listeNiveauxSolo.contains(file.getName())
                                && !listeNiveauxMultis.contains(file.getName())) {
                            choixNiveau.addItem(file.getName());
                        }
                    }
                } else if (multi) {
                    for (String niveau : listeNiveauxMultis) {
                        choixNiveau.addItem(niveau);
                    }
                } else {
                    for (String niveau : listeNiveauxSolo) {
                        choixNiveau.addItem(niveau);
                    }
                }
            }
        }
    }

    protected void actionCreerRoom() {
        /*
         * String ipServeur = JOptionPane.showInputDialog(jFrame,
         * "Entrez l'adresse IP du serveur :", "localhost");
         * if (ipServeur == null || ipServeur.trim().isEmpty()) {
         * return;
         * }
         */
        String ipServeur = "localhost";

        String roomId = java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        int difficulte = choixDifficulte.getSelectedIndex();
        double diff = getValeurDifficulte(difficulte);
        String niveau = "src/layouts/" + choixNiveau.getSelectedItem();

        try {
            // NOUVEAU : Passage de usernameConnecte
            new GameClient(ipServeur, 9081, null, niveau, diff, roomId, true, false, sessionCookie, usernameConnecte,
                    getScoreApiUrl());
            new RoomWindow(roomId);
            jFrame.dispose();

        } catch (Exception e) {
        }
    }

    protected void actionRejoindreRoom() {
        String roomId = roomIdField.getText().trim();

        /*
         * String ipServeur = JOptionPane.showInputDialog(jFrame,
         * "Entrez l'adresse IP du serveur :", "localhost");
         * if (ipServeur == null || ipServeur.trim().isEmpty()) {
         * return;
         * }
         */
        String ipServeur = "localhost";

        try {
            // NOUVEAU : Passage de usernameConnecte
            new GameClient(ipServeur, 9081, null, "", 0.0, roomId, false, false, sessionCookie, usernameConnecte,
                    getScoreApiUrl());
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
        } else if (index == 2) {
            return 0.7;
        } else if (index == 3) {
            return 0.9;
        }
        return 0.4;
    }
}
