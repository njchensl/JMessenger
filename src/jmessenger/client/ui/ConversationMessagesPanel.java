package jmessenger.client.ui;

import jmessenger.shared.ClientMessage;
import jmessenger.shared.TextMessage;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ConversationMessagesPanel extends JPanel {
    private List<ClientMessage> messages;

    public ConversationMessagesPanel(List<ClientMessage> messages) {
        super(new GridBagLayout());
        this.messages = messages;
        refresh();
    }

    public static void main(String[] args) throws InterruptedException {
        // TODO debug this first
        List<ClientMessage> m = new ArrayList<>();
        m.add(new TextMessage("T"));
        ConversationMessagesPanel pnl = new ConversationMessagesPanel(m);
        JFrame f = new JFrame();
        f.setContentPane(pnl);
        f.pack();
        f.setVisible(true);
        Thread.sleep(1000);
        m.add(new TextMessage("C"));
    }

    public void refresh() {
        this.removeAll();
        int gridy = 0;
        for (ClientMessage m : messages) {
            // render each message
            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = gridy;
            c.weightx = 1;
            c.weighty = 0;

            if (m instanceof TextMessage) {
                JButton btn = new JButton(((TextMessage) m).getText());
                if (m.isMyMessage()) {
                    btn.setBackground(Color.LIGHT_GRAY);
                } else {
                    btn.setBackground(Color.WHITE);
                }
                btn.setHorizontalAlignment(SwingConstants.LEFT);
                this.add(btn, c);
            }
            gridy++;

        }
        // the "spring" will push everything upwards so that everything is align to the top of the panel
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = gridy;
        c.weightx = 1;
        c.weighty = 1;
        JLabel lblEmpty = new JLabel();
        this.add(lblEmpty, c);
        this.revalidate();
    }

    public List<ClientMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<ClientMessage> messages) {
        this.messages = messages;
    }
}
