package edu.mvhs.togsoyun;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerThread implements Runnable {
    private Socket clientSocket;
    private GameManager manager;
    private BufferedReader in;
    private PrintWriter out;
    private String name;

    public ServerThread(Socket clientSocket, GameManager manager) {
        this.clientSocket = clientSocket;
        this.manager = manager;

        try {
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.out = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println("Error setting up streams: " + e.getMessage());
            closeEverything();
        }
    }

    public void run() {
        try {
            String clientMessage;
            mainloop:while ((clientMessage = in.readLine()) != null) {
                System.out.println(clientMessage);
                Character messageCode = clientMessage.charAt(0);
                clientMessage = clientMessage.substring(3);

                switch(messageCode) {
                    case 'N': // sent name
                        name = clientMessage;
                        if (manager.isAcceptingPlayers()) manager.add(this);
                        else {
                            System.out.println("Game is not accepting any new players");

                            break mainloop;
                        }

                        break;
                    case 'G': // game started
                        manager.startGame();

                        break;
                    case 'E': // player turn ended
                        manager.endTurn(clientMessage);

                        break;
                    case 'U': // player entered their guesses
                        manager.checkGuess(clientMessage, name);

                        break;
                }
            }
        } catch (IOException e) {
            System.out.println(name + " Error reading from client: " + e.getMessage());
        } finally {
            closeEverything();
        }
    }

    public String getName() {
        return name;
    }

    public void closeEverything() {
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (clientSocket != null) {
                clientSocket.close();
            }
        } catch (IOException e) {
            System.out.println("Error closing streams or socket: " + e.getMessage());
        } finally {
            if (name != null) manager.removeServerThread(this);
        }
    }

    public PrintWriter getOut(){
        return out;
    }

    public String toString() {
        return name;
    }
}
