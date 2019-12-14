package jmessenger.client;

import jmessenger.shared.ClientMessage;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Conversation {
    private int recipient;
    private List<ClientMessage> messages;

    public Conversation(int recipient) {
        this.recipient = recipient;
        this.messages = new ArrayList<>();
    }

    public int getRecipient() {
        return this.recipient;
    }

    public void setRecipient(int recipient) {
        this.recipient = recipient;
    }

    public void addMessage(@NotNull ClientMessage cm) {
        messages.add(cm);
    }

    public List<ClientMessage> getAllMessages() {
        return this.messages;
    }
}
