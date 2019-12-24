package jmessenger.client;

import jmessenger.client.ui.ConversationPanel;

import javax.swing.*;
import java.util.function.Consumer;

public class PluginButton extends JButton {
    public Consumer<ConversationPanel> action;
    private ConversationPanel cp;

    public PluginButton() {
        this.cp = null;
    }

    public PluginButton(ConversationPanel cp, Consumer<ConversationPanel> action) {
        this.cp = cp;
        this.action = action;
    }

    /**
     * makes a copy of the current button
     */
    public PluginButton clone(ConversationPanel cp) {
        PluginButton btn = new PluginButton(cp, this.action);
        btn.setText(this.getText());
        return btn;
    }

    public ConversationPanel getCp() {
        return cp;
    }

    public void setCp(ConversationPanel cp) {
        this.cp = cp;
    }
}