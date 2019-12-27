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
