package serveurPacman;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import com.google.gson.Gson;

import designPattern.StrategieInteractif;
import model.Agent;
import model.InitialisationPartieModele;
import model.Pacman;

public class ConnectionToClient {

    // private static final String DEFAULT_SCORE_API_URL = "http://46.101.67.203:8080//api/scores";
    private static final String DEFAULT_SCORE_API_URL = "http://localhost:8080//test/api/scores";

    private BufferedReader in;
    private PrintWriter out;
    private Socket socket;
    private Agent pacman;
    private SessionJeu session;
    private LaunchServer serveur;
    private String sessionCookie;
    private String username; // NOUVEAU : On stocke le pseudo
    private String scoreApiUrl = DEFAULT_SCORE_API_URL;
    private Gson gson = new Gson();

    public ConnectionToClient(Socket socket, LaunchServer serveur) throws IOException {
        this.socket = socket;
        this.serveur = serveur;
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        Thread read = new Thread() {
            public void run() {
                try {
                    String line;
                    boolean isInitialized = false;

                    while ((line = in.readLine()) != null) {
                        if (!isInitialized) {
                            try {
                                InitialisationPartieModele init = gson.fromJson(line, InitialisationPartieModele.class);
                                sessionCookie = init.getSessionCookie();
                                username = init.getUsername(); // NOUVEAU
                                if (init.getScoreApiUrl() != null && !init.getScoreApiUrl().trim().isEmpty()) {
                                    scoreApiUrl = init.getScoreApiUrl().trim();
                                }
                                serveur.assignerClientASession(ConnectionToClient.this, init);
                                isInitialized = true;
                                InitialisationPartieModele params = new InitialisationPartieModele(session.getNiveau(),
                                        session.getDifficulte(), session.getRoomId(), false, session.isRandom(), null, username);
                                write(gson.toJson(params));
                            } catch (Exception e) {
                            }
                        } else if (session != null && session.isPartieDemarree() && pacman != null) {
                            try {
                                int direction = Integer.parseInt(line);
                                if (pacman.getStrategie() instanceof StrategieInteractif) {
                                    ((StrategieInteractif) pacman.getStrategie()).setLastActionDirection(direction);
                                }
                            } catch (NumberFormatException e) {
                            }
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Client déconnecté.");
                    try {
                        socket.close();
                    } catch (IOException ex) {
                    }
                    session.retirerClient(ConnectionToClient.this);
                }
            }
        };
        read.start();
    }

    public void assignerPacman() {
        int pacmanAssignes = 0;
        for (int i = 0; i < session.getClientList().size(); i++) {
            ConnectionToClient client = session.getClientList().get(i);
            if (client.pacman != null) {
                pacmanAssignes++;
            }
        }

        int pacmansVu = 0;
        for (int i = 0; i < session.getVraiJeu().listeAgent.size(); i++) {
            if (session.getVraiJeu().listeAgent.get(i) instanceof Pacman) {
                if (pacmansVu == pacmanAssignes) {
                    this.pacman = session.getVraiJeu().listeAgent.get(i);
                    break;
                }
                pacmansVu++;
            }
        }

        if (pacmanAssignes + 1 == session.getNombreJoueursAttendus() && !session.isPartieDemarree()) {
            session.demarrerPartie();
        }
    }

    public void envoyerScore(int score) {
        if (sessionCookie == null || sessionCookie.isEmpty()) {
            System.out.println("[SCORE] Aucun cookie de session pour user=" + username + ", score non envoye.");
            return;
        }

        HttpURLConnection connection = null;
        try {
            URL url = new URL(scoreApiUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Cookie", sessionCookie);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            String requestBody = "{\"score\":" + score + "}";
            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(requestBody.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = connection.getResponseCode();
            String responseBody = readResponseBody(connection);
            System.out.println("[SCORE] user=" + username + " HTTP " + responseCode + " -> " + responseBody);
        } catch (IOException e) {
            System.out.println("[SCORE] Echec envoi score pour user=" + username + " : " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private String readResponseBody(HttpURLConnection connection) throws IOException {
        BufferedReader reader = null;
        try {
            if (connection.getErrorStream() != null) {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8));
            } else if (connection.getInputStream() != null) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            }

            if (reader == null) {
                return "";
            }

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    public void write(String message) {
        out.println(message);
    }

    public Agent getPacman() {
        return pacman;
    }

    public String getUsername() {
        return username;
    }

    public void setSession(SessionJeu session) {
        this.session = session;
    }
}
