package jmessenger.client.ui;

import javax.swing.*;
import java.awt.*;

import static jmessenger.client.ui.resources.Resources.rb;

public class MessagesPanel extends JPanel {
    private ConversationListPanel conversationListPanel;

    /**
     * create a messages pane
     */
    public MessagesPanel() {
        super(new GridBagLayout());
        initialize();
        conversationListPanel.updateConversations();
    }

    /**
     * initialize the messages panel
     */
    private void initialize() {
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.weighty = 0;
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.NORTH;
        JLabel lblTitle = new JLabel("   " + rb.getString("MESSAGES"));
        lblTitle.setFont(new Font("Arial", Font.PLAIN, 30));
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
        this.add(new JScrollPane(conversationListPanel), c);
        //this.add(conversationListPanel, c);
    }

    /**
     * @return the jpanel that contains all the list of conversations
     */
    public ConversationListPanel getConversationListPanel() {
        return conversationListPanel;
    }
}
