package edu.mvhs.togsoyun;

import java.util.Arrays;

public class GameManager {
    private DLList<ServerThread> activeThreads, playingThreads;
    private MyArrayList<Integer> left, right;
    private int[] blockWeights, leftTotal, rightTotal;
    private ServerThread currPlayer;
    private int currInd, numBlocks, leftWeight, rightWeight;
    private boolean acceptingPlayers;

    public GameManager() {
        reset();
    }

    private void reset() {
        activeThreads = new DLList<>();
        playingThreads = new DLList<>();
        numBlocks = 5;
        blockWeights = new int[numBlocks];
        leftTotal = new int[] {0, 0, 0, 0, 0};
        rightTotal = new int[] {0, 0, 0, 0, 0};
        currPlayer = null;
        currInd = 0;
        acceptingPlayers = true;
    }

    public void add(ServerThread serverThread) {
        activeThreads.add(serverThread);
        playingThreads.add(serverThread);

        // announce to all current players that another player has joined
        broadcastMessage("P: " + activeThreads.playersToString());
        if (activeThreads.size() == 2) {
            broadcastMessage("S: Enough players have joined.");
        } else if (activeThreads.size() > 2) {
            serverThread.getOut().println("S: Enough players have joined.");
        }
    }

    public void startGame() {
        // announce to all players that the game has started with the number of blocks
        // dictated by the amount of players
        int blockCount = 5;
        leftWeight = rightWeight = 0;
        int size = activeThreads.size();
        acceptingPlayers = false;

        left = new MyArrayList<>();
        right = new MyArrayList<>();

        if (size > 6) blockCount = 2;
        else if (size > 4) blockCount = 3;
        else if (size > 2) blockCount = 4;

        broadcastMessage("G: " + blockCount);

        // randomly set the weights of all the blocks
        MyHashSet<Integer> takenWeights = new MyHashSet<>();    // to prevent the blocks from having the same weights
        for (int i = 0; i < numBlocks; i ++) {
            int random = (int) (Math.random() * 20 + 1);
            while(takenWeights.contains(random)) {
                random = (int) (Math.random() * 20 + 1);
            }

            takenWeights.add(random);
            blockWeights[i] = random;
        }
        System.out.println(Arrays.toString(blockWeights));

        // announce the turn to the first player
        currInd = 0;
        currPlayer = playingThreads.get(currInd);
        broadcastMessage("T: " + currPlayer + "'s turn");
        currPlayer.getOut().println("Y: your turn");
    }

    public void endTurn(String addedBlocks) {
        // send the added blocks to everyone + check balance
        broadcastMessage("E: " + addedBlocks);
        checkBalance(addedBlocks);

        // change turn
        currInd = (currInd + 1) % playingThreads.size();
        currPlayer = playingThreads.get(currInd);
        broadcastMessage("T: " + currPlayer + "'s turn");
        currPlayer.getOut().println("Y: your turn");
    }

    private void checkBalance(String s) {
        // add to all blocks
        String[] addedBlocks = s.split(";");
        String[] addedBlocksLeft = addedBlocks[0].split(" ");
        String[] addedBlocksRight = addedBlocks[1].split(" ");
        for (String value : addedBlocksLeft) {
            if (value.length() > 0) {
                int num = Integer.parseInt(value);

                left.add(num);
                leftTotal[num] ++;
                leftWeight += blockWeights[num];
            }
        }
        for (String value : addedBlocksRight) {
            if (value.length() > 0) {
                int num = Integer.parseInt(value);

                right.add(num);
                rightTotal[num] ++;
                rightWeight += blockWeights[num];
            }
        }

        // broadcast balance
        String message = "B: ";
        if (leftWeight > rightWeight) {
            // left
            message += "left";
        } else if (leftWeight < rightWeight) {
            // right
            message += "right";
        } else {
            // equal
            boolean validBalance = false;
            for(int i = 0; i < numBlocks && !validBalance; i ++) {
                if (leftTotal[i] != rightTotal[i]) validBalance = true;
            }

            if (validBalance) message += "balanced";
            else message += "invalid balance";
        }

        broadcastMessage(message);
    }

    public void checkGuess(String s, String name) {
        String stringNums[] = s.split(" ");
        int [] guesses = new int[stringNums.length];
        boolean correct = true;
        for(int i = 0; i < stringNums.length && correct; i ++) {
            try {
                guesses[i] = Integer.parseInt(stringNums[i]);
                if (guesses[i] != blockWeights[i]) {
                    correct = false;
                }
            } catch (Exception ex) {
                System.out.println(ex);
            }
        }

        if (correct) { // win
            broadcastMessage("C: " + name + " guessed the weight of the blocks correctly - " + s);

            reset();
        } else { // continue playing
            broadcastMessage("I: " + name + " guessed the weight of the blocks incorrectly - " + s);
        }
    }

    public void removeTurn(ServerThread serverThread) { // finished using all blocks -> remove turn
        playingThreads.remove(serverThread);

        if (playingThreads.size() <= 0) {
            // game over
            broadcastMessage("L: All minerals have been used. As such, the players have lost.");

            reset();
        }
    }

    public void hint() {
        String hint = createHint();

        broadcastMessage("H: " + hint);
    }

    private String blockColor(int ind) {
        String color;

        switch (ind) {
            case 1: color = "Orange"; break;
            case 2: color = "Green"; break;
            case 3: color = "Blue"; break;
            case 4: color = "Purple"; break;
            default: color = "Red"; break;
        }

        return color;
    }

    private String createHint() {
        int ind1 = (int) (Math.random() * 5);
        int ind2 = (int) (Math.random() * 5);
        int action = (int) (Math.random() * 2);

        while (ind1 == ind2)  {
            ind2 = (int) (Math.random() * 5);
        }

        String hint;
        if (action == 0) {
            hint = blockColor(ind1) + "+" + blockColor(ind2) + "=" + (blockWeights[ind1] + blockWeights[ind2]);
        } else {
            hint = "|" + blockColor(ind1) + "-" + blockColor(ind2) + "|=" + Math.abs(blockWeights[ind1] - blockWeights[ind2]);
        }

        return hint;
    }

    public boolean isAcceptingPlayers() {
        return acceptingPlayers;
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

        if (activeThreads.size() == 0) reset();
        else if (currPlayer == serverThread) {
            // change turn
            currInd = (currInd + 1) % activeThreads.size();
            currPlayer = activeThreads.get(currInd);
            broadcastMessage("T: " + currPlayer + "'s turn");
            currPlayer.getOut().println("Y: your turn");
        }
    }
}
