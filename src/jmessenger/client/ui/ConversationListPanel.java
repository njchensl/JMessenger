package jmessenger.client.ui;

import jmessenger.client.Conversation;
import jmessenger.client.Messenger;
import jmessenger.shared.ClientMessage;
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
        int gridy = 0;
        for (Conversation co : cons) {
            GridBagConstraints c = new GridBagConstraints();
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
                    latest = ((TextMessage) cm).getText();
                }
            }
            JButton btn = new JButton("<html><font face=\"arial\"><p><font size=\"5\">" + co.getRecipient() + "</font></p><p><font size=\"3\">" + latest + "</font></p></font></html>");
            btn.setHorizontalAlignment(SwingConstants.LEFT);
            // clicking the button will open up a new conversation window
            btn.addActionListener((evt) ->
                    new JFrame() {{
                        setContentPane(new ConversationPanel(co));
                        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                        setPreferredSize(getContentPane().getPreferredSize());
                        pack();
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
                    }}.setVisible(true)
            );
            this.add(btn, c);

            gridy++;
        }
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = gridy;
        c.weightx = 1;
        c.weighty = 1;
        JLabel lblEmpty = new JLabel();
        this.add(lblEmpty, c);
    }
}
