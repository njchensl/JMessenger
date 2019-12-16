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
package jmessenger.server;

import jmessenger.shared.ClientMessage;
import jmessenger.shared.Message;
import jmessenger.shared.RSAUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.List;

public class User {
    private SecretKey key;
    private int id;
    private transient List<ClientMessage> inbox;
    private transient Connection connection; // the connection is not null when the user is online

    User(SecretKey key, int id) {
        this.key = key;
        this.id = id;
        this.inbox = new ArrayList<>();
    }

    @NotNull
    public SecretKey getKey() {
        return key;
    }

    void setKey(@NotNull SecretKey key) {
        this.key = key;
    }

    public int getId() {
        return id;
    }

    void setId(int id) {
        this.id = id;
    }

    @Nullable
    public Connection getConnection() {
        return connection;
    }

    void setConnection(@Nullable Connection connection) {
        this.connection = connection;
    }

    @NotNull
    public List<@NotNull ClientMessage> getInbox() {
        return inbox;
    }

    public synchronized void send(Message msg) {
        if (this.isOnline()) {
            this.connection.send(msg);
        } else {
            // offline
            if (msg instanceof ClientMessage) {
                inbox.add((ClientMessage) msg);
            }
            // ignored if not client message
        }
    }

    public boolean isOnline() {
        return connection != null;
    }

    @Override
    @NotNull
    public String toString() {
        return "USER\nUser ID: " + id + "User Public Key: " + RSAUtils.encode(key);
    }
}
