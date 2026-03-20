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
	public String getRoomId() {
		return roomId;
	}
	public String getSessionCookie() {
		return sessionCookie;
	}
	public boolean isCreation() {
		return isCreation;
	}
	public boolean isRandom() {
		return isRandom;
	}

	public InitialisationPartieModele(String choixNiveau, double difficulte) {
		this(choixNiveau, difficulte, null);
	}	

	public InitialisationPartieModele(String choixNiveau, double difficulte, String sessionCookie) {
		this.choixNiveau = choixNiveau;
		this.difficulte = difficulte;
		this.sessionCookie = sessionCookie;
        this.isRandom = true;
	}
	
	public InitialisationPartieModele(String choixNiveau, double difficulte, String roomId, boolean isCreation, boolean isRandom) {
		this(choixNiveau, difficulte, roomId, isCreation, isRandom, null);
	}

	public InitialisationPartieModele(String choixNiveau, double difficulte, String roomId, boolean isCreation, boolean isRandom, String sessionCookie) {
		this(choixNiveau, difficulte, sessionCookie);
		this.roomId = roomId;
		this.isCreation = isCreation;
		this.isRandom = isRandom;
	}
	
	private String choixNiveau;
	private double difficulte;
	private String roomId;
	private String sessionCookie;
	private boolean isCreation; //utilise que quand isRandom est false, c'est quand on cree la room (et non la rejoindre)
	private boolean isRandom; //si isRandom est true, ca veut dire qu'il fait une partie sans room
}
