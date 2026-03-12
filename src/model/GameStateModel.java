package model;

import java.util.ArrayList;

public class GameStateModel {
	/*
	 * Classe que le serveur va passer au client a chaque tour
	 * qui contient toutes les donnees utile a l'affichage
	 */
	private Maze maze;
	private ArrayList<Agent> listeAgent = new ArrayList<>();

    
	public void setMaze(Maze maze) {
		this.maze = maze;
	}
	public void setListeAgent(ArrayList<Agent> listeAgent) {
		this.listeAgent = listeAgent;
	}
	public Maze getMaze() {
		return maze;
	}
	public ArrayList<Agent> getListeAgent() {
		return listeAgent;
	}
}