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

import jmessenger.client.Conversation;
import jmessenger.client.Messenger;
import jmessenger.shared.ClientMessage;
import jmessenger.shared.PluginMessage;
import jmessenger.shared.TextMessage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.List;

import static jmessenger.client.ui.resources.Resources.rb;

public class ConversationListPanel extends JPanel {

    /**
     * create a conversation list panel
     */
    ConversationListPanel() {
        this.setLayout(new GridBagLayout());
        updateConversations();
    }

    /**
     * fetch the latest conversations and display them
     */
    public synchronized void updateConversations() {
        this.removeAll();
        List<Conversation> cons = Messenger.getInstance().getConversationList();
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 0;
        c.ipady = 5;
        JButton btnAdd = new JButton("<html><h1>+</h1></html>");
        btnAdd.addActionListener((evt) -> {
            // add a new conversation
            int recipient;
            try {
                recipient = Integer.parseInt(JOptionPane.showInputDialog(null, rb.getString("TALK_TO_WHOM"), rb.getString("NEW_CONVERSATION"), JOptionPane.INFORMATION_MESSAGE));
            } catch (Exception ignored) {
                return;
            }
            // see if a conversation already exists
            if (Messenger.getInstance().conversationAlreadyExists(recipient)) {
                JOptionPane.showMessageDialog(null, rb.getString("CONVERSATION_ALREADY_EXISTS"), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Messenger.getInstance().getConversationList().add(new Conversation(recipient));
            // refresh the list
            this.updateConversations();
            // revalidate the main frame
            Messenger.getInstance().getMainFrame().revalidate();
        });
        this.add(btnAdd, c);

        int gridy = 1;
        // draw each conversation
        for (Conversation co : cons) {
            c = new GridBagConstraints();
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = gridy;
            c.weightx = 1;
            c.weighty = 0;
            // html fonts
            ClientMessage cm = co.getLatestMessage();
            String latest = rb.getString("EMPTY_CONVERSATION");
            if (cm != null) {
                if (cm instanceof TextMessage) {
                    String s = ((TextMessage) cm).getText();
                    if (s.contains("<html>")) {
                        latest = rb.getString("HTML_MESSAGE");
                    } else {
                        if (s.length() > 20) {
                            s = s.substring(0, 20) + " ...";
                        }
                        latest = s;
                    }
                } else if (cm instanceof PluginMessage) {
                    latest = rb.getString("PLUGIN_MESSAGE");
                }
            }
            JButton btn = new JButton("<html><font face=\"arial\"><p><font size=\"5\">" + co.getRecipient() + "</font></p><p><font size=\"3\">" + latest + "</font></p></font></html>");
            btn.setHorizontalAlignment(SwingConstants.LEFT);
            // clicking the button will open up a new conversation window
            btn.addActionListener((evt) ->
                    new JFrame() {{
                        ConversationPanel cp = new ConversationPanel(co);
                        setContentPane(cp);
                        setIconImage(new ImageIcon(Messenger.class.getResource("icon.png")).getImage());
                        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
                        addWindowListener(new WindowListener() {
                            @Override
                            public void windowOpened(WindowEvent e) {
                            }

                            @Override
                            public void windowClosing(WindowEvent e) {
                                // stop the refreshing thread, then dispose
                                cp.onClose();
                                dispose();
                            }

                            @Override
                            public void windowClosed(WindowEvent e) {
                            }

                            @Override
                            public void windowIconified(WindowEvent e) {
                            }

                            @Override
                            public void windowDeiconified(WindowEvent e) {
                            }

                            @Override
                            public void windowActivated(WindowEvent e) {
                            }

                            @Override
                            public void windowDeactivated(WindowEvent e) {
                            }
                        });
                        setPreferredSize(new Dimension(800, 600));
                        setMinimumSize(new Dimension(600, 400));
                        pack();
                        /*
                        new Thread(() -> {
                            for (; ; ) {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                this.revalidate();
                            }
                        }).start();
                         */
                    }}.setVisible(true)
            );
            this.add(btn, c);

            gridy++;
        }

        // the "spring"
        c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = gridy;
        c.weightx = 1;
        c.weighty = 1;
        JLabel lblEmpty = new JLabel();
        this.add(lblEmpty, c);
    }
}
