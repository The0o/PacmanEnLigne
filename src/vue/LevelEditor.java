package vue;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class LevelEditor {

    public int lignes;
    public int colonnes;
    public JButton[][] gridBoutons;
    public char currentType = '%';
    public JFrame jFrame;

    public LevelEditor() {
        String largeur = JOptionPane.showInputDialog("Largeur de niveau");
        String hauteur = JOptionPane.showInputDialog("Hauteur de niveau");
        try {
            this.lignes = Integer.parseInt(hauteur);
            this.colonnes = Integer.parseInt(largeur);
        } catch (Exception e) {
            this.lignes = 20;
            this.colonnes = 20;
        }

        jFrame = new JFrame();
        jFrame.setTitle("Créateur de niveau Pacman");
        jFrame.setLayout(new BorderLayout());

        //-------------PALETTES DE COMMANDES-------------
        JPanel palettePanel = new JPanel();
        palettePanel.setBackground(Color.DARK_GRAY);

        addButton(palettePanel, "Mur", '%', Color.BLUE);
        addButton(palettePanel, "Gomme", '.', Color.BLUE);
        addButton(palettePanel, "Pacman", 'P', Color.BLUE);
        addButton(palettePanel, "Fantome", 'G', Color.BLUE);
        addButton(palettePanel, "Vide", ' ', Color.BLUE);
        jFrame.add(palettePanel, BorderLayout.NORTH);

        //-------------AFFICHAGE DES CASES-------------
        JPanel grid = new JPanel();
        grid.setLayout(new GridLayout(lignes, colonnes));
        gridBoutons = new JButton[lignes][colonnes];

        for (int y = 0; y < lignes; y++) {
            for (int x = 0; x < colonnes; x++) {
                JButton bouton = new JButton();
                bouton.setMargin(new Insets(0, 0, 0, 0));
                bouton.setFont(new Font("Arial", Font.BOLD, 15));
                bouton.setFocusable(false);

                if (x == 0 || x == colonnes - 1 || y == 0|| y == lignes - 1) {
                    updateBouton(bouton, '%');
                    bouton.setEnabled(false);
                }
                else {
                    updateBouton(bouton, ' ');
                    bouton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            updateBouton(bouton, currentType);
                        }
                    });
                }

                gridBoutons[y][x] = bouton;
                grid.add(bouton);
            }
        }
        jFrame.add(grid, BorderLayout.CENTER);

        //-------------BOUTON SAUVEGARDER-------------
        JButton saveBouton = new JButton("SAUVEGARDER LE NIVEAU");
        saveBouton.setFont(new Font("Arial", Font.BOLD, 20));
        saveBouton.setBackground(Color.GREEN);
        saveBouton.addActionListener(e -> saveNiveau());
        jFrame.add(saveBouton, BorderLayout.SOUTH);

        jFrame.setSize(800, 800);
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);
    }

    public void saveNiveau() {

        boolean hasPacman = false;
        boolean hasGhost = false;
        boolean hasFood = false;
        for (int y = 0; y < lignes; y++) {
            for (int x = 0; x < colonnes; x++) {
                char c = (char) gridBoutons[y][x].getClientProperty("c");
                if (c == 'P') hasPacman = true;
                if (c == 'G') hasGhost = true;
                if (c == '.') hasFood = true;
            }
        }

        if (!hasPacman) {
            JOptionPane.showMessageDialog(jFrame, "Il faut au moins selectionner un pacman", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!hasFood) {
            JOptionPane.showMessageDialog(jFrame, "Il faut au moins selectionner une gomme", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!hasGhost) {
            int reponse = JOptionPane.showConfirmDialog(jFrame, "Aucun fantome, etes-vous sur ?", "Validation", JOptionPane.YES_NO_OPTION);
            if (reponse == JOptionPane.NO_OPTION) return;
        }

        String name = JOptionPane.showInputDialog("Nom du fichier (sans .lay)");
        try (FileWriter writer = new FileWriter("src/layouts/" + name + ".lay")) {
            for (int y = 0; y < lignes; y++) {
                for (int x = 0; x < colonnes; x++) {
                    char c = (char) gridBoutons[y][x].getClientProperty("c");
                    writer.write(c);
                }
                writer.write("\n");
            }
            JOptionPane.showMessageDialog(jFrame, "Niveau enregistré avec succès");
            jFrame.dispose();
            new GameLauncher();
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public void updateBouton(JButton bouton, char c) {
        bouton.setText(String.valueOf(c));
        bouton.putClientProperty("c", c);
        if (c == '%') {
            bouton.setBackground(Color.BLUE);
        } else if (c == '.') {
            bouton.setBackground(Color.LIGHT_GRAY);
        } else if (c == 'P') {
            bouton.setBackground(Color.YELLOW);
        } else if (c == 'G') {
            bouton.setBackground(Color.RED);
        } else {
            bouton.setBackground(Color.BLACK);
        } 
    }

    public void addButton(JPanel palettePanel, String nom, char c, Color couleur) {
        JButton bouton = new JButton(nom);
        bouton.setBackground(couleur);
        if (c == '%') {
            bouton.setForeground(Color.WHITE);
        }
        bouton.addActionListener(e -> {
            this.currentType = c;
        });
        palettePanel.add(bouton);
    }
    
}
