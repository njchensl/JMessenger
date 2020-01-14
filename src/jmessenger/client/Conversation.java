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
package jmessenger.client;

import jmessenger.shared.ClientMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Conversation implements Serializable {
    private int recipient;
    private List<ClientMessage> messages;
    private long length;
    private long time;

    public Conversation(int recipient) {
        this.time = System.currentTimeMillis();
        this.length = 0;
        this.recipient = recipient;
        this.messages = new ArrayList<>();
    }

    /**
     * @return the recipient of the conversation
     */
    public int getRecipient() {
        return this.recipient;
    }

    /**
     * @param recipient the ID of the other end of the conversation
     */
    public void setRecipient(int recipient) {
        this.recipient = recipient;
    }

    /**
     * add a message to the conversation
     *
     * @param cm the client message to add
     */
    @SuppressWarnings("SynchronizeOnNonFinalField")
    public void addMessage(@NotNull ClientMessage cm) {
        this.time = System.currentTimeMillis();
        synchronized (messages) {
            messages.add(cm);
        }
        this.length = messages.size();
    }

    /**
     * @return all the messages stored in this conversation. DO NOT MODIFY THEM OUTSIDE IT
     */
    public List<@NotNull ClientMessage> getAllMessages() {
        return this.messages;
    }

    /**
     * @return the latest message
     */
    @Nullable
    public ClientMessage getLatestMessage() {
        if (messages.isEmpty()) {
            return null;
        }
        return messages.get(messages.size() - 1);
    }

    /**
     * @param length the current length of the messages as recorded by the GUI
     * @return if there is a new message
     */
    public boolean hasNewMessage(long length) {
        return this.length != length;
    }

    /**
     * @return the last time this conversation was updated
     */
    public long getTime() {
        return time;
    }
}
