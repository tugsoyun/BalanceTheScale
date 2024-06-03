package edu.mhvs.togsoyun;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

public class ClientScreen extends JPanel implements ActionListener {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Images imgs;
    private JButton[] increase, decrease;
    private JButton cont, back, next, submitGuesses, left, right, hint;
    private JLabel message, instructionLabel, hintLabel;
    private JTextPane players;
    private JScrollPane scrollPane;
    private JTextField[] guesses;
    private JTextField nameText;
    private Sound sound;
    private Image background;
    private String[] instructions;
    private String name, scaleCond, defaultFont, currSide;
    private MyArrayList<Integer> leftTotal, rightTotal, leftTemp, rightTemp;
    private int[] blockCount, leftBlockCount, rightBlockCount;
    private int numBlocks;
    private boolean gameOngoing, playerTurn, gameWon;

    public ClientScreen() throws IOException {
        this.setLayout(null);

        numBlocks = 5;
        imgs = new Images();
        sound = new Sound();
        defaultFont = "Helvetica";

        message = new JLabel("");
        message.setForeground(Color.WHITE);
        message.setBounds(300, 25, 400, 100);
        message.setFont(new Font(defaultFont, Font.BOLD, 20));
        message.setHorizontalAlignment(SwingConstants.CENTER);

        cont = new JButton();
        cont.setBounds(550, 435, 100, 40);
        cont.setFont(new Font(defaultFont, Font.BOLD, 20));
        cont.addActionListener(this);

        nameText = new JTextField();
        nameText.setBounds(385, 435, 150, 40);

        instructions = new String[] {
                "<html>Players must work together to balance the scale by placing minerals of different weight on it AND guess the weights of each< mineral to win.</html>",
                "<html>Players will be given a limited number of each mineral: red, orange, green, blue and purple.</html>",
                "<html>Each mineral will have a unique weight between one to twenty grams. The weight will be a whole number.</html>",
                "<html>During their turn, each player can place any number of minerals on any side of the scale. However, minerals placed from previous turns may not be removed.</html>",
                "<html>Once a player runs out of minerals, they will no longer be able to participate in the game. The actions of each player will be visible to others only after their turn is over.</html>",
                "<html>The scale will shift right, left or balance depending on the total weight of the minerals placed on each side. A scale with identical sides (same # of each mineral) will not be counted as a valid balance.</html>",
                "<html>Only when the scale is balanced, will the players be able to submit their guesses.</html>",
                "<html>If the guess is correct, the players win. If all players run out of minerals before they submit a correct guess, the players lose."
        };

        instructionLabel = new JLabel();
        instructionLabel.setBorder(new EmptyBorder(20, 20, 20, 20));
        instructionLabel.setFont(new Font(defaultFont, Font.PLAIN, 20));
        instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        instructionLabel.setBounds(200, 100, 600, 350);
        instructionLabel.setHorizontalTextPosition(JLabel.CENTER);
        instructionLabel.setVerticalTextPosition(JLabel.BOTTOM);
        instructionLabel.setBackground(new Color(175, 174, 178));
        instructionLabel.setOpaque(true);

        back = new JButton("<");
        back.setFont(new Font(defaultFont, Font.BOLD, 20));
        back.setHorizontalAlignment(SwingConstants.CENTER);
        back.setBounds(145, 260, 30, 30);
        back.addActionListener(this);

        next = new JButton(">");
        next.setFont(new Font(defaultFont, Font.BOLD, 20));
        next.setHorizontalAlignment(SwingConstants.CENTER);
        next.setBounds(825, 260, 30, 30);
        next.addActionListener(this);

        players = new JTextPane();
        players.setEditable(false);
        scrollPane = new JScrollPane(players);
        scrollPane.setBounds(150, 100, 300, 200);

        hint = new JButton("HINT!");
        hint.setFont(new Font(defaultFont, Font.PLAIN, 20));
        hint.setBounds(800, 40, 100, 40);
        hint.addActionListener(this);

        hintLabel = new JLabel();
        hintLabel.setForeground(Color.white);
        hintLabel.setBounds(750, 70, 200, 100);
        hintLabel.setFont(new Font(defaultFont, Font.PLAIN, 18));
        hintLabel.setHorizontalAlignment(SwingConstants.CENTER);

        left = new JButton("Left");
        left.setFont(new Font(defaultFont, Font.BOLD, 20));
        left.setBounds(350, 150, 100, 40);
        left.addActionListener(this);

        right = new JButton("Right");
        right.setFont(new Font(defaultFont, Font.BOLD, 20));
        right.setBounds(825, 150, 100, 40);
        right.addActionListener(this);

        submitGuesses = new JButton("Submit Guesses");
        submitGuesses.setBounds(50, 450, 200, 40);
        submitGuesses.setFont(new Font(defaultFont, Font.PLAIN, 18));
        submitGuesses.addActionListener(this);

        increase = new JButton[numBlocks];
        decrease = new JButton[numBlocks];
        guesses = new JTextField[numBlocks];
        for(int i = 0; i < numBlocks; i ++) {
            increase[i] = new JButton(Images.add);
            increase[i].setBounds(180, 60 + 75 * i, 25, 25);
            increase[i].addActionListener(this);
            increase[i].setBorder(null);

            decrease[i] = new JButton(Images.subtract);
            decrease[i].setBounds(50, 60 + 75 * i, 25, 25);
            decrease[i].addActionListener(this);
            decrease[i].setBorder(null);

            guesses[i] = new JTextField();
            guesses[i].setFont(new Font(defaultFont, Font.PLAIN, 18));
            guesses[i].setBounds(220, 60 + 75 * i, 30, 30);
        }

        reset();

        this.setFocusable(true);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(1000, 600);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // blocks are always visible
        if (gameOngoing) {
            setBackground(Color.BLACK);
            g.setColor(Color.WHITE);
            for (int i = 0; i < numBlocks; i++) {
                g.drawImage(Images.blocks[i], 88, 50 + 75 * i, null);
                g.drawString("X" + blockCount[i], 145, 80 + 75 * i);
            }

            // draw the scale
            int leftX, leftY, rightX, rightY;
            Image img;

            leftX = rightX = 350;
            leftY = rightY = 300;
            if (scaleCond.equals("left")) {
                img = Images.scaleLeft;
                leftX += 18; leftY += 162;
                rightX += 396; rightY += 15;
            } else if (scaleCond.equals("right")) {
                img = Images.scaleRight;
                leftX += 36; leftY += 15;
                rightX += 414; rightY += 162;
            } else {
                img = Images.scaleBalanced;
                leftX += 12; leftY += 117;
                rightX += 420; rightY += 117;
            }

            g.drawImage(img, 350, 300, null);
            drawBlocks(g, leftX, leftY, leftTotal, leftTemp);
            drawBlocks(g, rightX, rightY, rightTotal, rightTemp);
        } else {
            g.drawImage(background, 0, 0, null);
        }

        if (cont.getText().equals("ENTER")) {
            g.setColor(new Color(108, 108, 108));
            g.setFont(new Font(defaultFont, Font.ITALIC, 18));
            g.drawString("Enter Name:", 280, 460);
        }
    }

    private void drawBlocks(Graphics g, int startX, int startY, MyArrayList<Integer> perm, MyArrayList<Integer> temp) {
        int ind, n, sum, x, y;
        n = 0; sum = 12;

        ind = 0; x = startX; y = startY;
        if (perm != null) {
            for (; ind < perm.size(); ind ++) {
                if (ind == sum) {
                    n ++;
                    sum += (12 - n);
                    y = startY - 15 * n;
                    x = startX + 8 * n;
                }
                g.drawImage(Images.miniBlocks[perm.get(ind)], x, y, null);
                g.setColor(Color.BLACK);
                g.drawRect(x, y, 15, 15);
                x += 15;
            }
        }

        if (temp != null) {
            for (int i = 0; i < temp.size(); i ++) {
                if (i + ind == sum) {
                    n ++;
                    sum += (13 - n);
                    y = startY - 15 * n;
                    x = startX + 8 * n;
                }
                g.setColor(new Color(222, 210, 111));
                g.fillRect(x - 2, y - 2, 19, 17);
                g.drawImage(Images.miniBlocks[temp.get(i)], x, y, null);
                x += 15;
            }
        }
    }

    private void reset() {
        // phase 1 - setting all necessary variables + starting screen
        // game title + player enters name
        this.name = "";
        gameOngoing = false;
        gameWon = false;

        cont.setText("ENTER");
        background = Images.welcome;

        this.add(nameText);
        this.add(cont);
    }

    private void instructions() {
        // phase 2 - player can look through instructions
        // a slide show of instructions
        this.remove(nameText);

        message.setText("Game Instructions:");
        cont.setBounds(400, 475, 200, 50);
        cont.setText("PLAY");
        setInstructions(1);
        background = Images.background;

        this.add(message);
        this.add(instructionLabel);
        this.add(next);
    }

    private void setInstructions(int ind) {
        instructionLabel.setName("" + ind);
        instructionLabel.setText(instructions[ind - 1]);
        instructionLabel.setIcon(Images.inst[ind - 1]);
    }

    private void waitingRoom() {
        // phase 3 - player will wait for other players to connect
        // display all connected players + update as more join
        // players can choose their avatar
        this.remove(instructionLabel);
        this.remove(message);
        this.remove(back);
        this.remove(next);
        this.remove(cont);

        background = Images.background;

        cont.setText("START GAME!");

        this.add(scrollPane);
    }

    private void startGame(int initialBlocks) {
        // phase 4 - balance the scale will begin; players will be able to play during turn
        // block + scale graphics
        // display current turn
        playerTurn = false;

        this.remove(scrollPane);
        cont.setBounds(100, 500, 200, 50);
        this.remove(cont);

        gameOngoing = true;

        scaleCond = "invalid balance";

        leftTotal = new MyArrayList<>();
        rightTotal = new MyArrayList<>();
        blockCount = new int[numBlocks];
        for (int i = 0; i < numBlocks; i ++) {
            blockCount[i] = initialBlocks;
        }

        this.add(hint);
        this.add(hintLabel);
    }

    private void playerTurn() {
        // this player's turn
        // add/remove buttons + end turn button
        currSide = "left";
        playerTurn = true;
        leftTemp = new MyArrayList<>();
        rightTemp = new MyArrayList<>();
        leftBlockCount = new int[] {0,0,0,0,0};
        rightBlockCount = new int[] {0,0,0,0,0};

        cont.setText("END TURN");
        this.add(cont);
        this.add(left);
        this.add(right);

        boolean b = scaleCond.equals("balanced");
        for (int i = 0; i < numBlocks; i ++) {
            if (blockCount[i] > 0) this.add(increase[i]);

            if (b) {
                guesses[i].setText("");
                this.add(guesses[i]);
            }
        }
        if (b) this.add(submitGuesses);
    }

    private void endTurn() {
        out.println("E: " + leftTemp + "; " + rightTemp);

        leftTemp = new MyArrayList<>();
        rightTemp = new MyArrayList<>();

        this.remove(cont);
        this.remove(left);
        this.remove(right);
        this.remove(submitGuesses);

        boolean blocksLeft = false;
        for (int i = 0; i < numBlocks; i ++) {
            this.remove(decrease[i]);
            this.remove(increase[i]);
            this.remove(guesses[i]);

            if (blockCount[i] > 0) blocksLeft = true;
        }

        if (!blocksLeft) out.println("B: no blocks left");
    }

    private void updateScale(String s) {
        String[] addedBlocks = s.split(";");
        String[] addedBlocksLeft = addedBlocks[0].split(" ");
        String[] addedBlocksRight = addedBlocks[1].split(" ");
        for (String value : addedBlocksLeft) {
            if (value.length() > 0) {
                leftTotal.add(Integer.parseInt(value));
            }
        }
        for (String value : addedBlocksRight) {
            if (value.length() > 0) {
                rightTotal.add(Integer.parseInt(value));
            }
        }

        repaint();
    }

    private void endGame(boolean won) {
        gameWon = won;
        gameOngoing = false;

        if (won) {
            sound.setFile(1);
            sound.play();
            background = Images.win;
        } else {
            background = Images.lose;
        }

        this.remove(left);
        this.remove(right);
        this.remove(hint);
        this.remove(hintLabel);
        this.remove(message);
        cont.setBounds(400, 475, 200, 50);
        cont.setText("Restart Game");
        this.add(cont);
    }

    public void connect() {
        String serverAddress = "localhost";
        int serverPort = 1221;

        try {
            socket = new Socket(serverAddress, serverPort);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            String serverMessage;
            while ((serverMessage = in.readLine()) != null) {
                System.out.println(serverMessage);
                Character messageCode = serverMessage.charAt(0);
                serverMessage = serverMessage.substring(3);

                switch(messageCode) {
                    case 'P': // new player has joined -> update textarea
                        serverMessage = serverMessage.replace("<br>", "\n");
                        players.setText(serverMessage);

                        break;
                    case 'S': // enough players have joined to start the game
                        this.add(cont);

                        break;
                    case 'G': // game started
                        startGame(Integer.parseInt(serverMessage));

                        break;
                    case 'T': // another player's turn
                        message.setText(serverMessage);
                        this.add(message);

                        break;
                    case 'Y': // your turn
                        playerTurn();

                        break;
                    case 'E': // player has ended their turn
                        updateScale(serverMessage);

                        break;
                    case 'B': // balance
                        scaleCond = serverMessage;

                        break;
                    case 'C': // guessed correctly -> win game
                        message.setText("<html>" + serverMessage + "</html>");

                        Thread.sleep(5 * 1000); // 5 seconds

                        endGame(true);

                        break;
                    case 'I': // guessed incorrectly
                        message.setText("<html>" + serverMessage + "</html");

                        Thread.sleep(5 * 1000); // 5 seconds

                        if (playerTurn) {
                            playerTurn = false;
                            out.println("E: " + leftTemp + "; " + rightTemp);
                        }

                        break;
                    case 'L': // lost the game
                        message.setText("<html>" + serverMessage + "</html>");

                        Thread.sleep(5 * 1000); // 5 seconds

                        endGame(false);

                        break;
                    case 'H': // hint
                        hintLabel.setText(serverMessage);

                        break;
                }

                repaint();
            }
        } catch (UnknownHostException e) {
            System.err.println("Host unkown: " + serverAddress);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + serverAddress);
            System.exit(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == back) {
            int ind = Integer.parseInt(instructionLabel.getName());
            ind --;

            System.out.println("back ind: " + ind);


            setInstructions(ind);

            if (ind == 1) this.remove(back);
            else if (ind == instructions.length - 1) this.add(next);

            repaint();
        } else if (source == next) {
            int ind = Integer.parseInt(instructionLabel.getName());
            ind ++;

            System.out.println("next ind: " + ind);


            setInstructions(ind);

            if (ind == 2) this.add(back);
            else if (ind >= instructions.length) this.remove(next);

            repaint();
        } else if (source == cont) {
            if (cont.getText().equals("ENTER")) {
                // player entered their name -> instructions
                this.name = nameText.getText();

                instructions();
            } else if (cont.getText().equals("PLAY")) {
                // player finished reading instructions -> wait for other players
                out.println("N: " + name);

                waitingRoom();
            } else if (cont.getText().equals("START GAME!")){
                // started game -> relay message to server
                out.println("G: Game started by " + name);

            } else if (cont.getText().equals("END TURN")) {
                // player has ended their turn -> send all their actions to manager
                endTurn();
                playerTurn = false;
            } else if (cont.getText().equals("Restart Game")) {
                // restart -> return to waiting room screen
                out.println("N: " + name);

                waitingRoom();
            }

            repaint();
        } else if (source == left) {
            currSide = "left";

            for (int i = 0; i < numBlocks; i ++) {
                if (leftBlockCount[i] > 0) this.add(decrease[i]);
                else this.remove(decrease[i]);
            }

            repaint();
        } else if (source == right) {
            currSide = "right";

            for (int i = 0; i < numBlocks; i ++) {
                if (rightBlockCount[i] > 0) this.add(decrease[i]);
                else this.remove(decrease[i]);
            }

            repaint();
        } else if (source == submitGuesses) {
            String s = "";
            for(JTextField text : guesses) {
                s += text.getText() + " ";
            }
            out.println("U: " + s);

            endTurn();

            repaint();
        } else if (source == hint) {
            out.println("H: hints");
        } else {
            for (int i = 0; i < numBlocks; i ++) {
                if (source == decrease[i]) {
                    // TODO: fix
                    boolean success = false;
                    if (currSide.equals("left")) {
                        if (leftBlockCount[i] > 0) {
                            success = true;
                            leftTemp.remove((Integer) i);
                            leftBlockCount[i] --;
                            if (leftBlockCount[i] <= 0) this.remove(decrease[i]);
                        }
                    } else {
                        if (rightBlockCount[i] > 0) {
                            success = true;
                            rightTemp.remove((Integer) i);
                            rightBlockCount[i] --;
                            if (rightBlockCount[i] <= 0) this.remove(decrease[i]);
                        }
                    }
                    if (success) {
                        blockCount[i] ++;
                        if (blockCount[i] > 0) this.add(increase[i]);
                    }

                    repaint();

                    break;
                } else if (source == increase[i]) {
                    sound.setFile(0);
                    sound.play();
                    if (currSide.equals("left")) {
                        leftTemp.add(i);
                        leftBlockCount[i] ++;
                    } else {
                        rightTemp.add(i);
                        rightBlockCount[i] ++;
                    }
                    blockCount[i] --;
                    if (blockCount[i] <= 0) this.remove(increase[i]);
                    this.add(decrease[i]);

                    repaint();

                    break;
                }
            }
        }
    }
}
