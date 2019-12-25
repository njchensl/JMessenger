package jmessenger.client.ui;

import jmessenger.client.Conversation;
import jmessenger.client.Messenger;
import jmessenger.shared.ClientMessage;
import jmessenger.shared.PluginMessage;
import jmessenger.shared.TextMessage;

import javax.swing.*;
import java.awt.*;
import java.util.List;

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
                recipient = Integer.parseInt(JOptionPane.showInputDialog(null, "Who do you want to talk to?", "New Conversation", JOptionPane.INFORMATION_MESSAGE));
            } catch (Exception ignored) {
                return;
            }
            // see if a conversation already exists
            if (Messenger.getInstance().conversationAlreadyExists(recipient)) {
                JOptionPane.showMessageDialog(null, "This conversation already exists", "Error", JOptionPane.ERROR_MESSAGE);
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
            String latest = "Empty Conversation";
            if (cm != null) {
                if (cm instanceof TextMessage) {
                    String s = ((TextMessage) cm).getText();
                    if (s.contains("<html>")) {
                        latest = "[HTML Message]";
                    } else {
                        if (s.length() > 20) {
                            s = s.substring(0, 20) + " ...";
                        }
                        latest = s;
                    }
                } else if (cm instanceof PluginMessage) {
                    latest = "[Plugin Message]";
                }
            }
            JButton btn = new JButton("<html><font face=\"arial\"><p><font size=\"5\">" + co.getRecipient() + "</font></p><p><font size=\"3\">" + latest + "</font></p></font></html>");
            btn.setHorizontalAlignment(SwingConstants.LEFT);
            // clicking the button will open up a new conversation window
            btn.addActionListener((evt) ->
                    new JFrame() {{
                        setContentPane(new ConversationPanel(co));
                        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
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
