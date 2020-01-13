/*
 * The MIT License
 *
 * Copyright 2019 frche1699.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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
        this.setTitle("JMessenger");
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setMinimumSize(new Dimension(200, 500));
        this.setPreferredSize(new Dimension(350, 700));
        this.setIconImage(new ImageIcon(Messenger.class.getResource("icon.png")).getImage());
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
