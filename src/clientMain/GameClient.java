package clientMain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import com.google.gson.Gson;

public class GameClient {
    private ConnectionToServer server;
    private Socket socket;
    private Gson gson = new Gson();
    
    public static void main(String[] args) {
        try {
            GameClient client = new GameClient(args[0], Integer.parseInt(args[1]));
            //client.startClient(args[0], Integer.parseInt(args[1]));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public GameClient(String IPAdress, int port) throws IOException {
    	this.startClient(IPAdress, port);
    }
    
    public void startClient(String IPAddress, int port) throws IOException {
        socket = new Socket(IPAddress, port);
        server = new ConnectionToServer(socket);

        System.out.println("Tapez une phrase (ou 'quit' pour quitter) :");
        Scanner sc = new Scanner(System.in);
        
        while(true) {
            if (sc.hasNextLine()) {
                String ligne = sc.nextLine();
                if (ligne.equals("quit")) break;
                send(ligne);
            }
        }
        sc.close();
        socket.close();
    }

    private class ConnectionToServer {
        BufferedReader in;
        PrintWriter out;
        Socket socket;

        ConnectionToServer(Socket socket) throws IOException {
            this.socket = socket;
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            Thread read = new Thread() {
                public void run() {
                    try {
                        String line;
                        while((line = in.readLine()) != null) {
                            String message = gson.fromJson(line, String.class);
                            System.out.println("Message Reçu: " + message);
                        }
                    } catch(IOException e) { 
                        e.printStackTrace(); 
                    }
                }
            };
            read.start();
        }

        private void write(String obj) {
            out.println(gson.toJson(obj));
        }
    }

    public void send(String obj) {
        server.write(obj);
    }
}