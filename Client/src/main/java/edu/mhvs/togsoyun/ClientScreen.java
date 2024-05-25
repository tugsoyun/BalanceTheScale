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
    private JButton cont, back, next, submitGuesses, left, right;
    private JLabel message, instructionLabel;
    private JTextPane players;
    private JScrollPane scrollPane;
    private JTextField[] guesses;
    private JTextField nameText, leftText, rightText;
    private Color[] colors;
    private Sound sound;
    private String[] instructions;
    private String name, scaleCond, defaultFont, currSide;
    private MyArrayList<Integer> leftTotal, rightTotal, leftTemp, rightTemp;
    private int[] blockCount;
    private int numBlocks;
    private boolean gameOngoing, playerTurn;


    public ClientScreen() throws IOException {
        this.setLayout(null);

        numBlocks = 5;
        imgs = new Images();
        sound = new Sound();
        defaultFont = "Arial";

        message = new JLabel("");
        message.setBounds(300, 25, 400, 75);
        message.setFont(new Font(defaultFont, Font.BOLD, 20));
        message.setHorizontalAlignment(SwingConstants.CENTER);

        cont = new JButton();
        cont.setBounds(400, 500, 200, 50);
        cont.setFont(new Font(defaultFont, Font.BOLD, 20));
        cont.addActionListener(this);

        nameText = new JTextField();
        nameText.setBounds(475, 435, 150, 40);

        instructions = new String[] {
                "<html>Players must work together to balance the scale by placing minerals of different weight on it AND guess the weights of each< mineral to win.</html>",
                "<html>Players will be given red, yellow, green, blue and purple minerals, with 2 of each color</html>",
                "<html>Each mineral will have a unique weight between one to twenty grams. The weight will be a natural number.</html>",
                "<html>During their turn, each player must place at least 2 minerals on the scale. The actions of each player will be visible to others only after their turn is over.</html>",
                "<html>Minerals placed from previous turns may not be removed. A scale with identical sides (same # of each mineral) will not be counted as a valid balance.</html>",
                "<html>Then, the scale will shift right, left or balance depending on the total weight of the minerals placed on each side.</html>",
                "<html>Only when the scale is balanced, will the players be able to submit their guesses.</html>"
        };

        instructionLabel = new JLabel();
        instructionLabel.setBorder(new EmptyBorder(20, 20, 20, 20));
        instructionLabel.setFont(new Font(defaultFont, Font.PLAIN, 20));
        instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        instructionLabel.setBounds(200, 100, 600, 350);
        instructionLabel.setHorizontalTextPosition(JLabel.CENTER);
        instructionLabel.setVerticalTextPosition(JLabel.BOTTOM);
        instructionLabel.setBackground(new Color(194, 194, 194));
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
        scrollPane.setBounds(50, 25, 300, 350);

        left = new JButton("Left");
        left.setFont(new Font(defaultFont, Font.BOLD, 20));
        left.setBounds(350, 150, 100, 40);
        left.addActionListener(this);

        right = new JButton("Right");
        right.setFont(new Font(defaultFont, Font.BOLD, 20));
        right.setBounds(825, 150, 100, 40);
        right.addActionListener(this);

        leftText = new JTextField();
        leftText.setEditable(false);
        leftText.setFont(new Font(defaultFont, Font.BOLD, 20));
        leftText.setBounds(350, 225, 100, 50);

        rightText = new JTextField();
        rightText.setEditable(false);
        rightText.setFont(new Font(defaultFont, Font.BOLD, 20));
        rightText.setBounds(825, 225, 100, 50);

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
            rightText.setFont(new Font(defaultFont, Font.PLAIN, 18));
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

        setBackground(new Color(214, 217, 220));

        if (cont.getText().equals("ENTER")) {
            g.setColor(new Color(108, 108, 108));
            g.setFont(new Font(defaultFont, Font.ITALIC, 18));
            g.drawString("Enter Name:", 370, 460);
        }

        // blocks are always visible
        if (gameOngoing) {
            for (int i = 0; i < numBlocks; i++) {
                g.drawImage(Images.blocks[i], 88, 50 + 75 * i, null);
                g.drawString("X" + blockCount[i], 145, 80 + 75 * i);
            }

            // draw the scale
            if (scaleCond.equals("balanced")) {
                g.drawImage(Images.scaleBalanced, 500, 100, null);

            } else if (scaleCond.equals("left")) {
                g.drawImage(Images.scaleLeft, 500, 100, null);

            } else if (scaleCond.equals("right")) {
                g.drawImage(Images.scaleRight, 500, 100, null);

            }
        }
    }

    private void reset() {
        // phase 1 - setting all necessary variables + starting screen
        // game title + player enters name
        this.name = "";
        gameOngoing = false;
        colors = new Color[]{
                new Color(225, 93, 93),
                new Color(241, 170, 126),
                new Color(162, 199, 125),
                new Color(138, 222, 211),
                new Color(164, 156, 236)
        };

        message.setText("<html>Welcome to <br>BALANCE THE SCALE!</html>");
        cont.setText("ENTER");

        this.add(nameText);
        this.add(message);
        this.add(cont);
    }

    private void instructions() {
        // phase 2 - player can look through instructions
        // a slide show of instructions
        this.remove(nameText);

        message.setText("Game Instructions:");
        cont.setText("PLAY");
        setInstructions(1);

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

        cont.setText("START GAME!");

        this.add(scrollPane);
    }

    private void startGame(int initialBlocks) {
        // phase 4 - balance the scale will begin; players will be able to play during turn
        // block + scale graphics
        // display current turn
        playerTurn = false;

        this.remove(scrollPane);
        this.remove(cont);

        leftText.setText("0 0 0 0 0");
        rightText.setText("0 0 0 0 0");
        this.add(leftText);
        this.add(rightText);

        gameOngoing = true;

        scaleCond = "balanced";

        leftTotal = new MyArrayList<>();
        rightTotal = new MyArrayList<>();
        blockCount = new int[numBlocks];
        for (int i = 0; i < numBlocks; i ++) {
            blockCount[i] = initialBlocks;
        }
    }

    private void playerTurn() {
        // this player's turn
        // add/remove buttons + end turn button
        currSide = "left";
        playerTurn = true;
        leftTemp = new MyArrayList<>();
        rightTemp = new MyArrayList<>();

        cont.setText("END TURN");
        this.add(cont);
        this.add(left);
        this.add(right);
        for (int i = 0; i < numBlocks; i ++) {
            this.add(decrease[i]);
            this.add(increase[i]);

            if (scaleCond.equals("balanced")) {
                guesses[i].setText("");
                this.add(guesses[i]);
            }
        }
        if (scaleCond.equals("balanced")) {
            this.add(submitGuesses);
        }
    }

    private void endTurn() {
        this.remove(cont);
        this.remove(left);
        this.remove(right);
        this.remove(submitGuesses);
        for (int i = 0; i < numBlocks; i ++) {
            this.remove(decrease[i]);
            this.remove(increase[i]);
            this.remove(guesses[i]);
        }
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

        // TODO: DELETE ONCE GRAPHICS WORK
        int b[] = {0,0,0,0,0};
        for (int i = 0; i < leftTotal.size(); i ++) {
            b[leftTotal.get(i)] ++;
        }
        leftText.setText(b[0] + " " + b[1] + " " + b[2] + " " + b[3] + " " + b[4]);
        b = new int [] {0,0,0,0,0};
        for (int i = 0; i < rightTotal.size(); i ++) {
            b[rightTotal.get(i)] ++;
        }
        rightText.setText(b[0] + " " + b[1] + " " + b[2] + " " + b[3] + " " + b[4]);
    }

    private void endGame() {
        this.remove(leftText);
        this.remove(rightText);

        sound.setFile(1);
        sound.play();

        gameOngoing = false;

        message.setText("Congratulations, you won!");
        cont.setText("Restart Game");
        this.add(cont);
    }

    public void connect() {
        String serverAddress = "localhost";
        int serverPort = 1234;

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

                        endGame();

                        break;
                    case 'I': // guessed incorrectly
                        message.setText("<html>" + serverMessage + "</html");

                        Thread.sleep(5 * 1000); // 5 seconds

                        if (playerTurn) {
                            playerTurn = false;
                            out.println("E: " + leftTemp + "; " + rightTemp);
                        }

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

            setInstructions(ind);

            if (ind == 1) this.remove(back);
            else if (ind == instructions.length - 1) this.add(next);
        } else if (source == next) {
            int ind = Integer.parseInt(instructionLabel.getName());
            ind ++;

            setInstructions(ind);

            if (ind == 2) this.add(back);
            else if (ind == instructions.length) this.remove(next);
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

                out.println("E: " + leftTemp + "; " + rightTemp);
            }
        } else if (source == left) {
            currSide = "left";
        } else if (source == right) {
            currSide = "right";
        } else if (source == submitGuesses) {
            endTurn();

            String s = "";
            for(JTextField text : guesses) {
                s += text.getText() + " ";
            }

            out.println("U: " + s);
        } else {
            for (int i = 0; i < numBlocks; i ++) {
                if (source == decrease[i]) {
                    // TODO: when there arent enough blocks remove decrease button & vice versa
                    if (currSide.equals("left")) {
                        leftTemp.remove(i);
                    } else {
                        rightTemp.remove(i);
                    }
                    blockCount[i] ++;

                    break;
                } else if (source == increase[i]) {
                    sound.setFile(0);
                    sound.play();
                    if (currSide.equals("left")) {
                        leftTemp.add(i);
                    } else {
                        rightTemp.add(i);
                    }
                    blockCount[i] --;

                    break;
                }
            }
        }

        repaint();
    }
}
