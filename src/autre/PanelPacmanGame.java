package autre;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;

import javax.swing.JPanel;

import model.Maze;
import model.PositionAgent;

public class PanelPacmanGame extends JPanel {

    private static final long serialVersionUID = 1L;

    private Color wallColor = new Color(33, 33, 255); 
    private Color wallColor2 = new Color(0, 0, 100); 

    private double sizePacman = 1.1;
    private Color pacmansColor = Color.YELLOW;

    private Color ghostScarredColor = new Color(0, 0, 255); 

    private double sizeFood = 0.25;
    private Color colorFood = new Color(255, 184, 174);

    private double sizeCapsule = 0.6;
    private Color colorCapsule = new Color(255, 184, 174);

    private Maze m;

    private ArrayList<PositionAgent> pacmans_pos;
    private ArrayList<String> pacmansUsernames = new ArrayList<>(); // NOUVEAU
    private ArrayList<PositionAgent> ghosts_pos;

    private boolean ghostsScarred;

    public String messageFin = "";
    public Color couleur;
    
    public void afficherMessageFin(String message, Color couleur) {
        this.messageFin = message;
        this.couleur = couleur;
        this.repaint();
    }

    public PanelPacmanGame(Maze maze) {
        this.m = maze;
        pacmans_pos = this.m.getPacman_start();
        ghosts_pos = this.m.getGhosts_start();
        ghostsScarred = false;
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int dx = getSize().width;
        int dy = getSize().height;
        g2d.setColor(new Color(10, 10, 10)); 
        g2d.fillRect(0, 0, dx, dy);

        if (m == null) return;

        int sx = m.getSizeX();
        int sy = m.getSizeY();
        double stepx = dx / (double) sx;
        double stepy = dy / (double) sy;
        double posx = 0;

        for (int x = 0; x < sx; x++) {
            double posy = 0;
            for (int y = 0; y < sy; y++) {
                if (m.isWall(x, y)) {
                    g2d.setColor(wallColor2);
                    g2d.fillRoundRect((int) posx, (int) posy, (int) (stepx + 1), (int) (stepy + 1), 10, 10);
                    
                    g2d.setColor(wallColor);
                    g2d.setStroke(new BasicStroke(2));
                    g2d.drawRoundRect((int) posx + 2, (int) posy + 2, (int) (stepx - 4), (int) (stepy - 4), 8, 8);
                }
                if (m.isFood(x, y)) {
                    g2d.setColor(colorFood);
                    double nsx = stepx * sizeFood;
                    double nsy = stepy * sizeFood;
                    double npx = (stepx - nsx) / 2.0;
                    double npy = (stepy - nsy) / 2.0;
                    g2d.fillOval((int) (npx + posx), (int) (npy + posy), (int) (nsx), (int) nsy);
                }
                if (m.isCapsule(x, y)) {
                    g2d.setColor(colorCapsule);
                    double nsx = stepx * sizeCapsule;
                    double nsy = stepy * sizeCapsule;
                    double npx = (stepx - nsx) / 2.0;
                    double npy = (stepy - nsy) / 2.0;
                    
                    if (System.currentTimeMillis() % 600 < 300) {
                        g2d.fillOval((int) (npx + posx), (int) (npy + posy), (int) (nsx), (int) nsy);
                    } else {
                        g2d.drawOval((int) (npx + posx), (int) (npy + posy), (int) (nsx), (int) nsy);
                    }
                }
                posy += stepy;
            }
            posx += stepx;
        }

        for (int i = 0; i < pacmans_pos.size(); i++) {
            PositionAgent pos = pacmans_pos.get(i);
            String pseudo = (pacmansUsernames != null && i < pacmansUsernames.size()) ? pacmansUsernames.get(i) : "";
            drawPacmans(g2d, pos.getX(), pos.getY(), pos.getDir(), pacmansColor, pseudo); // NOUVEAU
        }

        Color[] ghostColors = { Color.RED, Color.PINK, Color.CYAN, Color.ORANGE };

        for (int i = 0; i < ghosts_pos.size(); i++) {
            PositionAgent pos = ghosts_pos.get(i);
            Color c = ghostColors[i % ghostColors.length];
            if (ghostsScarred) {
                drawGhosts(g2d, pos.getX(), pos.getY(), ghostScarredColor, true);
            } else {
                drawGhosts(g2d, pos.getX(), pos.getY(), c, false);
            }
        }

        if (!messageFin.isEmpty()) {
            g2d.setColor(new Color(0, 0, 0, 180));
            g2d.fillRect(0, 0, getWidth(), getHeight());
            
            g2d.setFont(new Font("Arial", Font.BOLD, 60));
            FontMetrics fm = g2d.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(messageFin)) / 2;
            int y = getHeight() / 2;
            
            g2d.setColor(Color.BLACK);
            g2d.drawString(messageFin, x + 4, y + 4);
            
            g2d.setColor(couleur);
            g2d.drawString(messageFin, x, y);
        }
    }

    void drawPacmans(Graphics2D g2d, int px, int py, int pacmanDirection, Color color, String pseudo) { // NOUVEAU PARAM
        if ((px != -1) || (py != -1)) {
            int dx = getSize().width;
            int dy = getSize().height;

            int sx = m.getSizeX();
            int sy = m.getSizeY();
            double stepx = dx / (double) sx;
            double stepy = dy / (double) sy;

            double posx = px * stepx;
            double posy = py * stepy;

            g2d.setColor(color);
            double nsx = stepx * sizePacman;
            double nsy = stepy * sizePacman;
            double npx = (stepx - nsx) / 2.0;
            double npy = (stepy - nsy) / 2.0;

            int mouthAngle = (int) (Math.abs(Math.sin(System.currentTimeMillis() / 150.0)) * 40); 
            
            int startAngle = 0;
            if (pacmanDirection == Maze.NORTH) startAngle = 90 + mouthAngle;
            else if (pacmanDirection == Maze.SOUTH) startAngle = 270 + mouthAngle;
            else if (pacmanDirection == Maze.EAST) startAngle = mouthAngle;
            else if (pacmanDirection == Maze.WEST) startAngle = 180 + mouthAngle;

            int arcAngle = 360 - (mouthAngle * 2);

            g2d.fillArc((int) (npx + posx), (int) (npy + posy), (int) (nsx), (int) nsy, startAngle, arcAngle);
            
            // NOUVEAU : Dessin du pseudo
            if (pseudo != null && !pseudo.isEmpty()) {
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 10));
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(pseudo);
                int textX = (int) (posx + (stepx - textWidth) / 2.0);
                int textY = (int) (posy - 2); // Légèrement au-dessus de la case
                
                // Ombre noire pour qu'il soit bien visible même sur une case claire
                g2d.setColor(Color.BLACK);
                g2d.drawString(pseudo, textX + 1, textY + 1);
                g2d.setColor(Color.WHITE);
                g2d.drawString(pseudo, textX, textY);
            }
        }
    }

    void drawGhosts(Graphics2D g2d, int px, int py, Color color, boolean isScarred) {
        if ((px != -1) || (py != -1)) {
            int dx = getSize().width;
            int dy = getSize().height;

            int sx = m.getSizeX();
            int sy = m.getSizeY();
            double stepx = dx / (double) sx;
            double stepy = dy / (double) sy;

            double posx = px * stepx;
            double posy = py * stepy;

            g2d.setColor(color);

            double nsx = stepx * sizePacman;
            double nsy = stepy * sizePacman;
            double npx = (stepx - nsx) / 2.0;
            double npy = (stepy - nsy) / 2.0;

            int x = (int) (posx + npx);
            int y = (int) (npy + posy);
            int w = (int) nsx;
            int h = (int) nsy;

            g2d.fillArc(x, y, w, h, 0, 180);
            g2d.fillRect(x, y + h / 2 - 1, w, h / 2 - 2);

            int waveOffset = (int) (System.currentTimeMillis() / 200 % 2) * 3;
            g2d.fillPolygon(
                new int[]{x, x, x + w/3, x + w/2, x + 2*w/3, x + w, x + w},
                new int[]{y + h/2, y + h + waveOffset, y + h - 3 + waveOffset, y + h + waveOffset, y + h - 3 + waveOffset, y + h + waveOffset, y + h/2},
                7
            );

            if (!isScarred) {
                g2d.setColor(Color.WHITE);
                g2d.fillOval(x + w / 4 - 2, y + h / 4, w / 3, h / 3);
                g2d.fillOval(x + 3 * w / 4 - w / 3 + 2, y + h / 4, w / 3, h / 3);

                g2d.setColor(Color.BLUE);
                g2d.fillOval(x + w / 4 + 1, y + h / 4 + 2, w / 6, h / 6);
                g2d.fillOval(x + 3 * w / 4 - w / 3 + 5, y + h / 4 + 2, w / 6, h / 6);
            } else {
                g2d.setColor(new Color(255, 184, 174));
                g2d.fillOval(x + w / 4, y + h / 3, 4, 4);
                g2d.fillOval(x + 3 * w / 4 - 4, y + h / 3, 4, 4);
                
                g2d.setStroke(new BasicStroke(2));
                g2d.drawPolyline(
                    new int[]{x + w/5, x + w/3, x + w/2, x + 2*w/3, x + 4*w/5},
                    new int[]{y + 2*h/3, y + h/2 + 2, y + 2*h/3, y + h/2 + 2, y + 2*h/3},
                    5
                );
            }
        }
    }

	public Maze getMaze(){
		return m;
	}
	
	public void setMaze(Maze maze){
		this.m = maze;
	}
	
	public void setGhostsScarred(boolean ghostsScarred) {
		this.ghostsScarred = ghostsScarred;
	}

	public ArrayList<PositionAgent> getPacmans_pos() {
		return pacmans_pos;
	}

	public void setPacmans_pos(ArrayList<PositionAgent> pacmans_pos) {
		this.pacmans_pos = pacmans_pos;				
	}
	
    // NOUVEAU
	public void setPacmansUsernames(ArrayList<String> pacmansUsernames) {
        this.pacmansUsernames = pacmansUsernames;
    }

	public ArrayList<PositionAgent> getGhosts_pos() {
		return ghosts_pos;
	}

	public void setGhosts_pos(ArrayList<PositionAgent> ghosts_pos) {
		this.ghosts_pos = ghosts_pos;
	}
}