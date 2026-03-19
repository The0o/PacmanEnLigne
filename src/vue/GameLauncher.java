package vue;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;

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
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import controller.ControllerPacmanGame;

public class GameLauncher {

    protected JComboBox<String> choixNiveau;
    protected JComboBox<String> choixDifficulte;
    public static Clip clip; 
    
    // On rend la fenêtre et le fond accessibles aux autres méthodes
    protected JFrame jFrame;
    protected JLabel backgroundLabel;

    public GameLauncher() {
        jFrame = new JFrame();
        jFrame.setTitle("Pacman");

        //-------------IMAGE BACKGROUND---------------
        ImageIcon image = new ImageIcon("src/image/pacmanImage.jpg");
        if (image.getIconWidth() != -1) {
            backgroundLabel = new JLabel(image);
        }
        else {
            backgroundLabel = new JLabel();
            jFrame.setPreferredSize(new Dimension(600, 600));
        }

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
        
        JLabel emailLabel = new JLabel("Email :");
        emailLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        emailLabel.setForeground(java.awt.Color.WHITE);
        emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JTextField emailField = new JTextField();
        emailField.setMaximumSize(new Dimension(250, 30));

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

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                String password = new String(passField.getPassword());

                if (verifierIdentifiants(email, password)) {
                    // Si OK, on charge le menu principal à la place
                    afficherMenuPrincipal(); 
                } else {
                    JOptionPane.showMessageDialog(jFrame, "Email ou mot de passe incorrect.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Ajout des éléments avec des espaces (RigidArea)
        backgroundLabel.add(Box.createRigidArea(new Dimension(0, 50)));
        backgroundLabel.add(titre);
        backgroundLabel.add(Box.createRigidArea(new Dimension(0, 30)));
        backgroundLabel.add(emailLabel);
        backgroundLabel.add(emailField);
        backgroundLabel.add(Box.createRigidArea(new Dimension(0, 15)));
        backgroundLabel.add(passLabel);
        backgroundLabel.add(passField);
        backgroundLabel.add(Box.createRigidArea(new Dimension(0, 30)));
        backgroundLabel.add(loginButton);

        // Rafraîchissement de la fenêtre
        backgroundLabel.revalidate();
        backgroundLabel.repaint();
    }

    // Méthode de vérification
    private boolean verifierIdentifiants(String email, String password) {
        return email.equals("joueur@pacman.fr") && password.equals("123456");
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
                JFrame roomFrame = new JFrame("Nouvelle room serveur");
                roomFrame.setSize(400, 300);
                roomFrame.setLocationRelativeTo(null);

                roomFrame.setVisible(true);
            }
        });

        modePanel.add(radioSolo);
        modePanel.add(Box.createRigidArea(new Dimension(10, 0)));
        modePanel.add(radioMulti);
        modePanel.add(Box.createRigidArea(new Dimension(30, 0)));
        modePanel.add(creerRoomBtn);

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
        File folder = new File("src/layouts");
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".lay"));
            if (files.length > 0) {
                for (File file : files) {
                    choixNiveau.addItem(file.getName());
                }
            }
        }
    }

    public void lancerJeu() throws Exception {
        String choixFichier = (String) choixNiveau.getSelectedItem();
        String path = "src/layouts/" + choixFichier;

        int difficulte = choixDifficulte.getSelectedIndex();
        double diff = 0.4;
        if (difficulte == 0) {
            diff = 0.1;
        } else if (difficulte == 2) {
            diff = 0.7;
        } else if (difficulte == 3) {
            diff = 0.9;
        }
        new ControllerPacmanGame(path, diff);
    }
}