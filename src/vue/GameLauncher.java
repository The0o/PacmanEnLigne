package vue;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;


import controller.ControllerPacmanGame;

public class GameLauncher {

    JComboBox<String> choixNiveau;
    JComboBox<String> choixDifficulte;
    public static Clip clip; //static pour n'avoir qu'une musique si on revient au enu principal

    public GameLauncher() {
        JFrame jFrame = new JFrame();
        jFrame.setTitle("Pacman");

        //-------------IMAGE BACKGROUND---------------
        ImageIcon image = new ImageIcon("image/pacmanImage.jpg");
        JLabel backgroundLabel;
        if (image.getIconWidth() != -1) {
            backgroundLabel = new JLabel(image);
        }
        else {
            backgroundLabel = new JLabel();
            jFrame.setPreferredSize(new Dimension(600, 600));
        }

        //-------------LABEL DE TITRE-------------
        JLabel titre = new JLabel("PACMAN");
        titre.setFont(new Font("Monospaced", Font.BOLD, 20));
        titre.setForeground(java.awt.Color.WHITE);
        titre.setAlignmentX(Component.CENTER_ALIGNMENT);
        backgroundLabel.add(titre);

        backgroundLabel.setLayout(new BoxLayout(backgroundLabel, BoxLayout.Y_AXIS));
        jFrame.setContentPane(backgroundLabel);

        //-------------CHOIX DU NIVEAU-------------
        JLabel choixNiveauLabel = new JLabel("Choisir niveau :");
        choixNiveauLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
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
        diffculteLabel.setFont(new Font("Monospaced", Font.BOLD, 14));
        choixDifficulte.setMaximumSize(new Dimension(300, 40));
        choixDifficulte.setSelectedIndex(1);
        backgroundLabel.add(choixDifficulte);

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

        //-------------BOUTON EDITER-------------
        JButton editerBouton = new JButton("CREER NIVEAU");
        editerBouton.setFont(new Font("Monospaced", Font.BOLD, 24));
        editerBouton.setAlignmentX(Component.CENTER_ALIGNMENT);
        editerBouton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        editerBouton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new LevelEditor();
            }
        });

        backgroundLabel.add(editerBouton);
        jFrame.pack();
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);

        launchMusic();
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
        new ControllerPacmanGame(path, diff);
    }
    
}
