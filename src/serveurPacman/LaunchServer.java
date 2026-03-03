package serveurPacman;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;
import com.google.gson.Gson;

public class LaunchServer {

    public static void main(String[] args) throws IOException {
        new LaunchServer(Integer.parseInt(args[0]));
    }
    
    private CopyOnWriteArrayList<ConnectionToClient> clientList;
    private ServerSocket serverSocket;
    private Gson gson = new Gson();

    public LaunchServer(int port) throws IOException {
        clientList = new CopyOnWriteArrayList<ConnectionToClient>();
        serverSocket = new ServerSocket(port);

        while(true) {
            try {
                Socket s = serverSocket.accept();
                clientList.add(new ConnectionToClient(s));
            } catch(IOException e) { 
                e.printStackTrace();
            }
        }
    }
    
    private class ConnectionToClient {
        BufferedReader in;
        PrintWriter out;
        Socket socket;

        ConnectionToClient(Socket socket) throws IOException {
            this.socket = socket;
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            Thread read = new Thread() {
                public void run() {
                    try {
                        String line;
                        while((line = in.readLine()) != null) {
                            String texte = gson.fromJson(line, String.class);
                            System.out.println("Message Received: " + texte);
                            
                            int longueur = texte.length();
                            String reponse = "Le texte '" + texte + "' fait " + longueur + " caractères.";

                            sendToAll(reponse);
                        }
                    } catch(IOException e) { 
                        e.printStackTrace();
                        System.out.println("Client déconnecté.");
                        try { socket.close(); } catch (IOException ex) {} 
                        clientList.remove(ConnectionToClient.this);
                    }
                }
            };
            read.start();
        }

        public void write(String message) {
            out.println(gson.toJson(message));
        }
    }

    public void sendToOne(int index, String message) throws IndexOutOfBoundsException {
        clientList.get(index).write(message);
    }

    public void sendToAll(String message) {
        for(ConnectionToClient client : clientList)
            client.write(message);
    }
}