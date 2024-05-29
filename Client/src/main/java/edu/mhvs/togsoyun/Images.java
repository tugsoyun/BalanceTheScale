package edu.mhvs.togsoyun;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class Images {
    static Icon[] inst;
    static Icon add, subtract;
    static Image[] blocks;
    static Image background, welcome, win, lose, scaleBalanced, scaleLeft, scaleRight;

    public Images() throws IOException {
        inst = new Icon[7];
        for (int i = 0; i < inst.length; i ++) {
            inst[i] = new ImageIcon(ImageIO.read(getClass().getResource("/assets/inst" + (i + 1) + ".png")).getScaledInstance(440, 180, Image.SCALE_SMOOTH));
        }

        blocks = new Image[5];
        for(int i = 0; i < blocks.length; i ++) {
            blocks[i] = ImageIO.read(getClass().getResource("/assets/block" + i + ".png")).getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        }

        add = new ImageIcon(ImageIO.read(getClass().getResource("/assets/plus.png")).getScaledInstance(25, 25, Image.SCALE_SMOOTH));
        subtract = new ImageIcon(ImageIO.read(getClass().getResource("/assets/minus.png")).getScaledInstance(25, 25, Image.SCALE_SMOOTH));

        welcome = ImageIO.read(getClass().getResource("/assets/welcome.jpg")).getScaledInstance(1000, 600, Image.SCALE_SMOOTH);
        background = ImageIO.read(getClass().getResource("/assets/background.png")).getScaledInstance(1000, 600, Image.SCALE_SMOOTH);
        win = ImageIO.read(getClass().getResource("/assets/win.png")).getScaledInstance(1000, 600, Image.SCALE_SMOOTH);
        lose = ImageIO.read(getClass().getResource("/assets/lose.png")).getScaledInstance(1000, 600, Image.SCALE_SMOOTH);

        scaleBalanced = ImageIO.read(getClass().getResource("/assets/balanced.png")).getScaledInstance(621, 300, Image.SCALE_SMOOTH);
        scaleLeft = ImageIO.read(getClass().getResource("/assets/left.png")).getScaledInstance(621, 300, Image.SCALE_SMOOTH);
        scaleRight = ImageIO.read(getClass().getResource("/assets/right.png")).getScaledInstance(621, 300, Image.SCALE_SMOOTH);
    }
}
