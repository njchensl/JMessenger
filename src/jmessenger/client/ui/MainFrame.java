package jmessenger.client.ui;

import jmessenger.client.Messenger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
                // ignored
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
                // ignored
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
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                showPop(e);
            }

            private void showPop(MouseEvent e) {
                new PopupMenu() {
                    {
                        add(new MenuItem("ITEM"));
                    }
                }.show(e.getComponent(), e.getX(), e.getY());
            }
        });
        this.pack();
    }

}
