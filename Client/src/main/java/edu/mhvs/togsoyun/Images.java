package edu.mhvs.togsoyun;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

public class Images {
    static Icon[] inst;
    static Image scaleBalanced, scaleLeft, scaleRight;

    public Images() throws IOException {
        inst = new Icon[7];
        for (int i = 0; i < inst.length; i++) {
            inst[i] = new ImageIcon(ImageIO.read(getClass().getResource("/assets/inst" + (i + 1) + ".png")).getScaledInstance(440, 180, Image.SCALE_SMOOTH));
        }

        scaleLeft = ImageIO.read(getClass().getResource("/assets/scale.png")).getScaledInstance(300, 300, Image.SCALE_SMOOTH);
    }
}
