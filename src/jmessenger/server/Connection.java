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

import jmessenger.shared.*;
import org.apache.commons.lang3.SerializationUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Connection {
    private Socket s;
    private User user;
    private In in;
    private Out out;

    public Connection(@NotNull Socket s) throws IOException {
        this.s = s;
        this.in = new In(new ObjectInputStream(s.getInputStream()), this);
        this.out = new Out(new ObjectOutputStream(s.getOutputStream()), this);
        initialize();
    }

    private void initialize() {
        new Thread(in).start();
        new Thread(out).start();
    }

    public synchronized void send(Message msg) {
        this.out.send(msg);
    }

    @Nullable
    public User getUser() {
        return user;
    }

    public void setUser(@Nullable User user) {
        this.user = user;
    }

    public In getIn() {
        return in;
    }

    public Out getOut() {
        return out;
    }

    public void end() {
        in.stop();
        out.stop();
        // uncouple the user and the connection
        this.user.setConnection(null);
        this.user = null;
        try {
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // remove its reference so that the garbage collector would be able to remove it from memory
        Server.getInstance().getConnections().remove(this);
    }
}

class In implements Runnable {
    private ObjectInputStream in;
    private Connection connection;
    private boolean running;

    public In(ObjectInputStream in, @NotNull Connection c) {
        this.in = in;
        this.connection = c;
        this.running = true;
    }

    @Override
    public void run() {
        // listening for new messages regardless if the user is null
        while (running) {
            try {
                EncryptedMessage e = (EncryptedMessage) in.readObject();
                byte[] data = e.getMessage();
                byte[] decrypted = RSAUtils.decrypt(data, Server.getInstance().getPrivateKey());
                Message msg = SerializationUtils.deserialize(decrypted);
                if (msg instanceof ServerMessage) {
                    // login
                    if (msg instanceof LoginMessage) {
                        LoginMessage lm = (LoginMessage) msg;
                        PublicKey userPublicKey = lm.getPublicKey();
                        User u = Server.getInstance().getUserByPublicKey(userPublicKey);
                        if (u == null) {
                            System.err.println("Unauthorized access\nPublic key: " + RSAUtils.encode(userPublicKey));
                            // stop the connection
                            this.connection.end();
                        }
                        // bind the connection to the user
                        this.connection.setUser(u);
                        u.setConnection(this.connection);
                        // send all messages
                        this.connection.getOut().clearInbox();
                        // login complete
                    } else if (msg instanceof RegistrationMessage) {

                    }
                } else {
                    // client message

                }
            } catch (IOException | ClassNotFoundException | NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException ex) {
                ex.printStackTrace();
            }
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        running = false;
    }
}

class Out implements Runnable {
    private ObjectOutputStream out;
    private Queue<@NotNull Message> buffer;
    private Connection connection;
    private boolean running;

    public Out(ObjectOutputStream out, Connection c) {
        this.out = out;
        this.connection = c;
        this.buffer = new ConcurrentLinkedQueue<>();
        this.running = true;
    }

    @Override
    public void run() {
        while (running) {
            Message msg = buffer.poll();
            if (msg == null) {
                continue;
            }
            if (connection.getUser() != null) {
                PublicKey pub = connection.getUser().getPublicKey();
                byte[] data = SerializationUtils.serialize(msg);
                try {
                    byte[] encrypted = RSAUtils.encrypt(data, pub);
                    EncryptedMessage em = new EncryptedMessage(encrypted);
                    out.writeObject(em);
                } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | IOException e) {
                    e.printStackTrace();
                    // assuming that the connection is not possible
                    this.connection.end();
                }
            }
        }
        try {
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * THE USER MUST NOT BE NULL WHEN THIS METHOD IS CALLED
     */
    public void clearInbox() {
        User user = this.connection.getUser();
        if (!user.getInbox().isEmpty()) {
            buffer.addAll(user.getInbox());
            user.getInbox().clear();
        }
    }

    public void send(@NotNull Message msg) {
        this.buffer.add(msg);
    }

    public void stop() {
        running = false;
    }
}