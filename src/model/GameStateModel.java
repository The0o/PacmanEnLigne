package model;

import java.util.ArrayList;

public class GameStateModel {
	/*
	 * Classe que le serveur va passer au client a chaque tour
	 * qui contient toutes les donnees utiles a l'affichage
	 */
	private Maze maze;
	private ArrayList<PositionAgent> positionsPacmans = new ArrayList<>();
	private ArrayList<String> pacmansUsernames = new ArrayList<>(); // NOUVEAU
	private ArrayList<PositionAgent> positionsFantomes = new ArrayList<>();
	private boolean effraye;
    
	public void setMaze(Maze maze) {
		this.maze = maze;
	}
	public Maze getMaze() {
		return maze;
	}
	public boolean getEffraye() {
		return effraye;
	}
	public void setEffraye(boolean effraye) {
		this.effraye = effraye;
	}
	public ArrayList<PositionAgent> getPositionsPacmans() {
		return positionsPacmans;
	}
	public void setPositionsPacmans(ArrayList<PositionAgent> positionsPacmans) {
		this.positionsPacmans = positionsPacmans;
	}
	public ArrayList<String> getPacmansUsernames() {
		return pacmansUsernames;
	}
	public void setPacmansUsernames(ArrayList<String> pacmansUsernames) {
		this.pacmansUsernames = pacmansUsernames;
	}
	public ArrayList<PositionAgent> getPositionsFantomes() {
		return positionsFantomes;
	}
	public void setPositionsFantomes(ArrayList<PositionAgent> positionsFantomes) {
		this.positionsFantomes = positionsFantomes;
	}
}