package jmessenger.client.ui;

import javax.swing.*;
import java.awt.*;

public class MessagesPanel extends JPanel {
    private ConversationListPanel conversationListPanel;

    public MessagesPanel() {
        super(new GridBagLayout());
        initialize();
        conversationListPanel.updateConversations();
    }

    private void initialize() {
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.weighty = 0;
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.NORTH;
        JLabel lblTitle = new JLabel("   Messages");
        lblTitle.setFont(new Font("Arial", Font.PLAIN, 25));
        c.ipadx = 280;
        c.ipady = 50;
        this.add(lblTitle, c);

        c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0;
        c.weighty = 1;
        c.gridx = 0;
        c.gridy = 1;
        conversationListPanel = new ConversationListPanel();
        this.add(conversationListPanel, c);
    }


    public ConversationListPanel getConversationListPanel() {
        return conversationListPanel;
    }
}
