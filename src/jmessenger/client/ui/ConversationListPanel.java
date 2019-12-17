package jmessenger.client.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;
import javax.swing.*;
import jmessenger.client.Conversation;
import jmessenger.client.Messenger;

public class ConversationListPanel extends JPanel {

    public ConversationListPanel() {
        this.setLayout(new GridBagLayout());
        updateConversations();
    }

    private void test() {
        JButton btn1, btn2;
        btn1 = new JButton("text");
        btn2 = new JButton("More text");

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 0;
        this.add(btn1, c);

        c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1;
        c.weighty = 0;
        this.add(btn2, c);

        c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 1;
        c.weighty = 1;
        JLabel lblEmpty = new JLabel();
        this.add(lblEmpty, c);
    }

    public void updateConversations() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        List<Conversation> cons = Messenger.getInstance().getConversationList();
        int gridy = 0;
        for (Conversation co : cons) {
            ConversationItem item = new ConversationItem(co);
            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = gridy;
            c.weightx = 1;
            c.weighty = 0;
            this.add(item, c);
            gridy++;
        }
    }
}
