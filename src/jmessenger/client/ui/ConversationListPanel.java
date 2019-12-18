package jmessenger.client.ui;

import jmessenger.client.Conversation;
import jmessenger.client.Messenger;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ConversationListPanel extends JPanel {

    /**
     * create a conversation list panel
     */
    public ConversationListPanel() {
        this.setLayout(new GridBagLayout());
        updateConversations();

        //test();
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

    /**
     * fetch the latest conversations and display them
     */
    public synchronized void updateConversations() {
        this.removeAll();
        List<Conversation> cons = Messenger.getInstance().getConversationList();
        int gridy = 0;
        for (Conversation co : cons) {
            /*
            ConversationItem item = new ConversationItem(co);
            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = gridy;
            c.weightx = 1;
            c.weighty = 0;
            this.add(item, c);
            */
            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = gridy;
            c.weightx = 1;
            c.weighty = 0;
            // TODO modify the HTML code to make the font fit
            this.add(new JButton("<html><p>" + co.getRecipient() + "</p><p>LATEST MESSAGE</p></html>"), c);


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
