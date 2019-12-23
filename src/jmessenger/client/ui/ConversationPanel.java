/*
 * The MIT License
 *
 * Copyright 2019 gagao9815.
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
import jmessenger.shared.TextMessage;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * @author gagao9815
 */
public class ConversationPanel extends javax.swing.JPanel {
    private Conversation conversation;
    private long length;

    private javax.swing.JButton sendBtn;
    private javax.swing.JPanel pnlConversationMessages;
    private javax.swing.JPanel pnlPlugin;
    private javax.swing.JPanel pnlTitle;
    private javax.swing.JTextArea txtInput;

    /**
     * Creates new form ConversationPanel
     */
    public ConversationPanel(@NotNull Conversation conversation) {
        super(new GridBagLayout());
        this.length = 0;
        this.conversation = conversation;
        List<ClientMessage> messages = conversation.getAllMessages();

        this.pnlConversationMessages = new ConversationMessagesPanel(messages, null);
        JScrollPane sp = new JScrollPane(pnlConversationMessages);
        ((ConversationMessagesPanel) pnlConversationMessages).setScrollPane(sp);

        txtInput = new JTextArea();
        txtInput.setFont(new Font("Arial", Font.PLAIN, 17));
        sendBtn = new JButton("SEND");
        sendBtn.setFont(new Font("Arial", Font.PLAIN, 17));
        pnlTitle = new JPanel();
        JLabel lblTitle = new JLabel("Conversation with " + conversation.getRecipient());
        lblTitle.setHorizontalAlignment(SwingConstants.LEFT);
        lblTitle.setHorizontalTextPosition(SwingConstants.LEFT);
        lblTitle.setFont(new Font("Arial", Font.PLAIN, 25));
        pnlTitle.add(lblTitle);
        pnlPlugin = new JPanel();
        initializeComponents();

        this.sendBtn.addActionListener((evt) -> {
            //JOptionPane.showMessageDialog(null, "");
            // make sure it is not empty
            String txt = txtInput.getText();
            if (!txt.trim().equals("")) {
                // send the text message
                TextMessage tm = new TextMessage(txt);
                tm.setMyMessage(true);
                tm.setRecipient(conversation.getRecipient());
                this.conversation.addMessage(tm);
                Messenger.getInstance().send(tm);
                txtInput.setText("");
                ((ConversationMessagesPanel) pnlConversationMessages).refresh();
                JPanel pnl = ((MainPanel) Messenger.getInstance().getMainFrame().getContentPane()).getMainPanel();
                if (pnl instanceof MessagesPanel) {
                    ((MessagesPanel) pnl).getConversationListPanel().updateConversations();
                }
                Messenger.getInstance().getMainFrame().revalidate();
            }
        });

        // refreshing when message is received
        new Thread(() -> {
            for (; ; ) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (conversation.hasNewMessage(length)) {
                    ((ConversationMessagesPanel) pnlConversationMessages).refresh();
                    this.length = conversation.getAllMessages().size();
                }
            }
        }).start();
    }

    public void initializeComponents() {

        this.removeAll();

        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 0;
        c.gridx = 0;
        c.gridy = 0;
        c.ipady = 15;
        this.add(pnlTitle, c);

        c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        c.gridx = 0;
        c.gridy = 1;
        ((ConversationMessagesPanel) pnlConversationMessages).refresh();
        pnlConversationMessages.revalidate();
        this.add(((ConversationMessagesPanel) pnlConversationMessages).getScrollPane(), c);

        c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 0;
        c.gridx = 0;
        c.gridy = 2;
        c.ipady = 25;
        this.add(pnlPlugin, c);

        c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 0;
        c.gridx = 0;
        c.gridy = 3;
        c.ipady = 50;
        DefaultContextMenu.addDefaultContextMenu(txtInput);
        this.add(new JScrollPane(txtInput), c);

        c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 0;
        c.gridx = 0;
        c.gridy = 4;
        c.ipady = 5;
        this.add(sendBtn, c);
    }


    /**
     * @return the pnlConversationMessages
     */
    public javax.swing.JPanel getPnlConversationMessages() {
        return pnlConversationMessages;
    }

    /**
     * @param pnlConversationMessages the pnlConversationMessages to set
     */
    public void setPnlConversationMessages(javax.swing.JPanel pnlConversationMessages) {
        this.pnlConversationMessages = pnlConversationMessages;
    }

    /**
     * @return the pnlPlugin
     */
    public javax.swing.JPanel getPnlPlugin() {
        return pnlPlugin;
    }

    /**
     * @param pnlPlugin the pnlPlugin to set
     */
    public void setPnlPlugin(javax.swing.JPanel pnlPlugin) {
        this.pnlPlugin = pnlPlugin;
    }
}
