package jmessenger.client.ui;

import jmessenger.client.Messenger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

public class MainFrame extends JFrame {

    public MainFrame() {
        this.setContentPane(new MainPanel());
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setMinimumSize(new Dimension(200, 500));
        this.setPreferredSize(new Dimension(350, 700));
        this.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
                // ignored
            }

            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    Messenger.getInstance().close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                System.exit(0);
            }

            @Override
            public void windowClosed(WindowEvent e) {
                // ignored
            }

            @Override
            public void windowIconified(WindowEvent e) {
                // gc
                System.gc();
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
                // gc
                System.gc();
            }

            @Override
            public void windowActivated(WindowEvent e) {
                // ignored
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
                // ignored
            }
        });
        this.pack();
    }

}
