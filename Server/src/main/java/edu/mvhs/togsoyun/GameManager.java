package edu.mvhs.togsoyun;

public class GameManager {
    private DLList<ServerThread> activeThreads;
    private MyHashMap<Character, String> outgoingMessages;

    public GameManager() {
        activeThreads = new DLList<>();

//        outgoingMessages = new MyHashMap<>();
//        outgoingMessages.put('P', "New Player Added");
    }

    public void add(ServerThread serverThread) {
        activeThreads.add(serverThread);

        // announce to all current players that another player has joined
        broadcastMessage("P: " + activeThreads.playersToString());
    }

    public void broadcastMessage(String message) {
        for (int i = 0; i < activeThreads.size(); i ++) {
            activeThreads.get(i).getOut().println(message);
        }
    }

    public void stop() {
        for (int i = 0; i < activeThreads.size(); i ++) {
            activeThreads.get(i).closeEverything();
        }
    }

    public void removeServerThread(ServerThread serverThread){
        activeThreads.remove(serverThread);
    }
}
