package edu.mvhs.togsoyun;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main (String[] args) {
        int portNumber = 1234;
        GameManager game = new GameManager();

        try {
            ServerSocket serverSocket = new ServerSocket(portNumber);
            System.out.println("Server is listening on port " + portNumber);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New player connected!");

                ServerThread serverThread = new ServerThread(clientSocket, game);
                Thread thread = new Thread(serverThread);
                thread.start();

                game.add(serverThread);
                game.announcePlayer(serverThread);
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            e.printStackTrace();
        }
    }
}
