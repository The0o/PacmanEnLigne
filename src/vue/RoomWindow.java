package vue;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class RoomWindow {

    private JFrame frame;
    private String roomId;
    private int nbPersonnes;
    
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

    public RoomWindow(String roomId) {
        this.roomId = roomId;
        this.nbPersonnes = 1;

        frame = new JFrame("Room Multijoueur");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        
        //-------------IMAGE BACKGROUND---------------
        JLabel mainPanel;
        ImageIcon image = loadImageIcon("/image/pacmanImage.jpg", "src/image/pacmanImage.jpg", "image/pacmanImage.jpg");
        if (image.getIconWidth() != -1) {
            mainPanel = new JLabel(image);
        }
        else {
            mainPanel = new JLabel();
            mainPanel.setOpaque(true);
            mainPanel.setBackground(Color.BLACK);
            frame.setPreferredSize(new Dimension(600, 600));
        }

        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        frame.setContentPane(mainPanel);

        //-------------TITRE-------------
        JLabel titre = new JLabel("SALLE D'ATTENTE");
        titre.setFont(new Font("Monospaced", Font.BOLD, 24));
        titre.setForeground(Color.WHITE);
        titre.setAlignmentX(Component.CENTER_ALIGNMENT);

        //-------------ID DE LA ROOM-------------
        JLabel idLabel = new JLabel("ID de la Room : " + this.roomId);
        idLabel.setFont(new Font("Monospaced", Font.PLAIN, 18));
        idLabel.setForeground(Color.WHITE);
        idLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        //-------------NOMBRE DE PERSONNES-------------
        JLabel personnesLabel = new JLabel("Nombre de joueurs : " + nbPersonnes);
        personnesLabel.setFont(new Font("Monospaced", Font.PLAIN, 16));
        personnesLabel.setForeground(Color.WHITE);
        personnesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        //-------------BOUTON QUITTER-------------
        JButton quitterBtn = new JButton("QUITTER LA ROOM");
        quitterBtn.setFont(new Font("Monospaced", Font.BOLD, 14));
        quitterBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        quitterBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        quitterBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose(); 
            }
        });

        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        mainPanel.add(titre);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        mainPanel.add(idLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(personnesLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 40)));
        mainPanel.add(quitterBtn);        
        frame.setVisible(true);
    }
}