package model;

public class InitialisationPartieModele {
	/*
	 * Classe que le client va donner au serveur UNE FOIS
	 * pour indiquer le niveau, la difficulte et le nom d'utilisateur choisis
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
	public String getUsername() {
		return username;
	}
	public String getScoreApiUrl() {
		return scoreApiUrl;
	}
	public boolean isCreation() {
		return isCreation;
	}
	public boolean isRandom() {
		return isRandom;
	}

	public InitialisationPartieModele(String choixNiveau, double difficulte) {
		this(choixNiveau, difficulte, null, null);
	}	

	public InitialisationPartieModele(String choixNiveau, double difficulte, String sessionCookie, String username) {
		this.choixNiveau = choixNiveau;
		this.difficulte = difficulte;
		this.sessionCookie = sessionCookie;
		this.username = username;
        this.isRandom = true;
	}
	
	public InitialisationPartieModele(String choixNiveau, double difficulte, String roomId, boolean isCreation, boolean isRandom) {
		this(choixNiveau, difficulte, roomId, isCreation, isRandom, null, null);
	}

	public InitialisationPartieModele(String choixNiveau, double difficulte, String roomId, boolean isCreation, boolean isRandom, String sessionCookie, String username) {
		this(choixNiveau, difficulte, sessionCookie, username);
		this.roomId = roomId;
		this.isCreation = isCreation;
		this.isRandom = isRandom;
	}
	
	private String choixNiveau;
	private double difficulte;
	private String roomId;
	private String sessionCookie;
	private String username;
	private String scoreApiUrl;
	private boolean isCreation; // utilise que quand isRandom est false, c'est quand on cree la room
	private boolean isRandom;   // si isRandom est true, ca veut dire qu'il fait une partie sans room

	public void setScoreApiUrl(String scoreApiUrl) {
		this.scoreApiUrl = scoreApiUrl;
	}
}
