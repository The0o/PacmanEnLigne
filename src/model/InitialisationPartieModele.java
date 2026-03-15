package model;

import java.util.ArrayList;

import designPattern.StrategieAgent;

public class InitialisationPartieModele {
	/*
	 * Classe que le client va donner au serveur UNE FOIS
	 * pour indiquer le niveau et la difficulte choisi
	 */
	
	public String getChoixNiveau() {
		return choixNiveau;
	}
	public double getDifficulte() {
		return difficulte;
	}
	public InitialisationPartieModele(String choixNiveau, double difficulte) {
		this.choixNiveau = choixNiveau;
		this.difficulte = difficulte;
	}
	
	private String choixNiveau;
	private double difficulte;
    
}