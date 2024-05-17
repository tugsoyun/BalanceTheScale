package edu.mhvs.togsoyun;

import javax.swing.*;
import java.io.IOException;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter your name: ");
        String name = sc.nextLine();

        JFrame frame = new JFrame("Balance the Scales - " + name);

        ClientScreen cs = new ClientScreen(name);
        frame.add(cs);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        cs.connect();
    }
}