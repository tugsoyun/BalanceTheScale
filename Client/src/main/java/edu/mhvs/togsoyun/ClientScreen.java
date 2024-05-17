package edu.mhvs.togsoyun;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientScreen extends JPanel implements ActionListener {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Images imgs;
    private JButton[] increase, decrease;
    private JButton start, back, next, send, left, right;
    private JLabel message, instructionLabel;
    private JTextPane players;
    private JScrollPane scrollPane;
    private Color[] colors;
    private String[] instructions;
    private String name, scaleCond;
    private int[] blockCount, lBlocks, rBlocks;
    private int numBlocks, lTotal, rTotal, lAdd, rAdd;
    private boolean gameOngoing;


    public ClientScreen(String name) throws IOException {
        this.setLayout(null);

        this.name = name;
        gameOngoing = false;
        numBlocks = 5;
        colors = new Color[]{
                new Color(225, 93, 93),
                new Color(241, 170, 126),
                new Color(162, 199, 125),
                new Color(138, 222, 211),
                new Color(164, 156, 236)
        };

        imgs = new Images();

        instructions = new String[] {
                "<html>Players must work together to balance the scale by placing minerals of different weight on it AND guess the weights of each< mineral to win.</html>",
                "<html>Players will be given red, yellow, green, blue and purple minerals, with 2 of each color</html>",
                "<html>Each mineral will have a unique weight between one to twenty grams. The weight will be a natural number.</html>",
                "<html>During their turn, each player must place at least 2 minerals on the scale. The actions of each player will be visible to others only after their turn is over.</html>",
                "<html>Minerals placed from previous turns may not be removed. A scale with identical sides (same # of each mineral) will not be counted as a valid balance.</html>",
                "<html>Then, the scale will shift right, left or balance depending on the total weight of the minerals placed on each side.</html>",
                "<html>Only when the scale is balanced, will the players be able to submit their guesses.</html>"
        };

        message = new JLabel("Game Instructions:");
        message.setBounds(300, 25, 400, 50);
        message.setFont(new Font("Arial", Font.BOLD, 20));
        message.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(message);

        instructionLabel = new JLabel();
        instructionLabel.setBorder(new EmptyBorder(20, 20, 20, 20));
        instructionLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        instructionLabel.setBounds(200, 100, 600, 350);
        instructionLabel.setHorizontalTextPosition(JLabel.CENTER);
        instructionLabel.setVerticalTextPosition(JLabel.BOTTOM);
        instructionLabel.setBackground(new Color(194, 194, 194));
        instructionLabel.setOpaque(true);
        this.add(instructionLabel);
        setInstructions(1);

        start = new JButton("PLAY!");
        start.setBounds(400, 500, 200, 50);
        start.setFont(new Font("Arial", Font.BOLD, 20));
        start.addActionListener(this);
        this.add(start);

        back = new JButton("<");
        back.setFont(new Font("Arial", Font.BOLD, 20));
        back.setHorizontalAlignment(SwingConstants.CENTER);
        back.setBounds(145, 260, 30, 30);
        back.addActionListener(this);

        next = new JButton(">");
        next.setFont(new Font("Arial", Font.BOLD, 20));
        next.setHorizontalAlignment(SwingConstants.CENTER);
        next.setBounds(825, 260, 30, 30);
        this.add(next);
        next.addActionListener(this);

        players = new JTextPane();
        players.setEditable(false);
        players.setText(name + "<br>");

        scrollPane = new JScrollPane(players);
        scrollPane.setBounds(50, 25, 300, 350);

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

        // blocks are always visible
        if (gameOngoing) {
            for (int i = 0; i < numBlocks; i++) {
                g.setColor(colors[i]);
                g.fillRect(88, 50 + 75 * i, 50, 50);
                g.drawString("X" + blockCount[i], 145, 80 + 75 * i);
            }


            // draw the scale
            if (scaleCond.equals("balanced")) {
                g.drawImage(Images.scaleLeft, 500, 250, null);

            } else if (scaleCond.equals("left")) {
                g.drawImage(Images.scaleLeft, 500, 250, null);

            } else if (scaleCond.equals("right")) {
                g.drawImage(Images.scaleLeft, 500, 250, null);

            }
        }
    }

    public void connect() {
        String serverAddress = "localhost";
        int serverPort = 1234;

        try {
            socket = new Socket(serverAddress, serverPort);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            out.println(name);

            String serverMessage;
            while (true) {
                serverMessage = in.readLine();
                System.out.println(serverMessage);
                if (serverMessage.charAt(0) == 'P') {
                    players.setText(serverMessage);
                }
            }
        } catch (UnknownHostException e) {
            System.err.println("Host unkown: " + serverAddress);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + serverAddress);
            System.exit(1);
        }
    }

    public void startGame(int initialBlocks) {
        gameOngoing = true;

        scaleCond = "balanced";

        blockCount = new int[numBlocks];
        lBlocks = new int[numBlocks];
        rBlocks = new int[numBlocks];
        increase = new JButton[numBlocks];
        decrease = new JButton[numBlocks];

        for (int i = 0; i < numBlocks; i ++) {
            blockCount[i] = initialBlocks;
            lBlocks[i] = 0;
            rBlocks[i] = 0;

            increase[i] = new JButton("+");
            increase[i].setBounds(180, 65 + 75 * i, 20, 20);
            increase[i].addActionListener(this);
            this.add(increase[i]);

            decrease[i] = new JButton("-");
            decrease[i].setBounds(50, 65 + 75 * i, 20, 20);
            decrease[i].addActionListener(this);
            this.add(decrease[i]);
        }

        lTotal = rTotal = lAdd = rAdd = 0;
    }

    private void setInstructions(int ind) {
        instructionLabel.setName("" + ind);
        instructionLabel.setText(instructions[ind - 1]);
        instructionLabel.setIcon(Images.inst[ind - 1]);
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
        } else if (source == start) {
            System.out.println("Started");
            if (message.getText().contains("Instructions")) {
                instructionLabel.setText("");
                this.remove(instructionLabel);
                this.remove(message);
                this.remove(back);
                this.remove(next);

                start.setText("START GAME!");
                this.add(scrollPane);

                repaint();
            } else {
                this.remove(scrollPane);
                this.remove(start);
                System.out.println("game start :D");
                out.println("Start Game");
            }
        }

        repaint();
    }
}
