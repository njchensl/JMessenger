package jmessenger.client.ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        this.setContentPane(new MainPanel());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setMinimumSize(new Dimension(200, 500));
        this.setPreferredSize(new Dimension(350, 700));
        this.pack();
    }

}
