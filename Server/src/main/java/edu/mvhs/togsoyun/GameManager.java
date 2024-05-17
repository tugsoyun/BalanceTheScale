package edu.mvhs.togsoyun;

public class GameManager {
    private DLList<ServerThread> serverThreads;
    private MyHashMap<Character, String> outgoingMessages;
    private String players;

    public GameManager() {
        serverThreads = new DLList<>();
        players = "<html>";

//        outgoingMessages = new MyHashMap<>();
//        outgoingMessages.put('P', "New Player Added");
    }

    public void add(ServerThread serverThread) {
        serverThreads.add(serverThread);
    }

    public void announcePlayer(ServerThread serverThread) {
        System.out.println(serverThreads);
        // announce to all current players that another player has joined
        players += ", " + serverThread.getName();
        broadcastMessage("P: " + players + "</html>");
    }

    public void broadcastMessage(String message) {
        for (int i = 0; i < serverThreads.size(); i ++) {
            serverThreads.get(i).getOut().println(message);
        }
    }

    public void stop() {
        for (int i = 0; i < serverThreads.size(); i ++) {
            serverThreads.get(i).closeEverything();
        }
    }

    public void removeServerThread(ServerThread serverThread){
        serverThreads.remove(serverThread);
    }
}
