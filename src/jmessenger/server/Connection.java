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
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Connection {
    private Socket s;
    private User user;
    private In in;
    private Out out;

    Connection(@NotNull Socket s) throws IOException {
        System.out.println("Initializing connection...");
        this.s = s;
        System.out.println("Creating I/O streams");

        this.out = new Out(new ObjectOutputStream(s.getOutputStream()), this);
        System.out.println("Output object created");

        InputStream is = s.getInputStream();
        System.out.println("Input stream created");
        ObjectInputStream oin = new ObjectInputStream(is);
        System.out.println("Object input stream created");
        this.in = new In(oin, this);
        System.out.println("Input object created");


        initialize();
        System.out.println("Connection initialized");
    }

    private void initialize() {
        System.out.println("Starting I/O threads");
        new Thread(in).start();
        new Thread(out).start();
        System.out.println("I/O threads started");
    }

    synchronized void send(Message msg) {
        this.out.send(msg);
    }

    @Nullable
    User getUser() {
        return user;
    }

    void setUser(@Nullable User user) {
        this.user = user;
    }

    In getIn() {
        return in;
    }

    Out getOut() {
        return out;
    }

    void end() {
        in.stop();
        out.stop();
        // uncouple the user and the connection
        if (this.user != null) {
            this.user.setConnection(null);
            this.user = null;
        }
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

    In(ObjectInputStream in, @NotNull Connection c) {
        System.out.println("creating in");
        this.in = in;
        this.connection = c;
        this.running = true;
    }

    @Override
    public void run() {
        // listening for new messages regardless if the user is null
        while (running) {
            System.out.println("Listening for incoming messages");
            try {
                EncryptedMessage e = (EncryptedMessage) in.readObject();
                byte[] data = e.getMessage();
                byte[] decrypted;
                if (e.isUsingAES()) {
                    assert this.connection.getUser() != null;
                    decrypted = AESUtils.decrypt(data, this.connection.getUser().getKey());
                } else {
                    decrypted = RSAUtils.decrypt(data, Server.getInstance().getPrivateKey());
                }

                Message msg = SerializationUtils.deserialize(decrypted);
                System.out.println("Message received:");
                System.out.println(msg);
                if (msg instanceof ServerMessage) {
                    // login
                    if (msg instanceof LoginMessage) {
                        LoginMessage lm = (LoginMessage) msg;
                        SecretKey userKey = lm.getKey();
                        User u = Server.getInstance().getUserByKey(userKey);
                        if (u == null) {
                            System.err.println("Unauthorized access\nPublic key: " + RSAUtils.encode(userKey));
                            // stop the connection
                            this.connection.end();
                            break;
                        }
                        // bind the connection to the user
                        this.connection.setUser(u);
                        u.setConnection(this.connection);
                        // send all messages
                        this.connection.getOut().clearInbox();
                        // login complete
                    } else if (msg instanceof RegistrationMessage) {
                        // registration
                        RegistrationMessage rm = (RegistrationMessage) msg;
                        SecretKey userKey = rm.getKey();
                        // generate ID
                        String sID = RandomStringUtils.random((int) (Math.random() * 3) + 6, false, true);
                        // add this user
                        User u = new User(userKey, Integer.parseInt(sID));
                        System.out.println("New user registered:\n" + u);
                        this.connection.setUser(u);
                        u.setConnection(this.connection);
                        Server.getInstance().addUser(u);

                        // registration response
                        RegistrationResponseMessage rrm = new RegistrationResponseMessage(u.getId());
                        this.connection.getOut().send(rrm);
                    }
                } else if (msg instanceof ClientMessage) {
                    // client message
                    ClientMessage cm = (ClientMessage) msg;
                    int recipient = cm.getRecipientID();
                    assert this.connection.getUser() != null;
                    int sender = this.connection.getUser().getId();
                    cm.setRecipient(sender);
                    User rec = Server.getInstance().getUserByID(recipient);
                    if (rec == null) {
                        // no user with a matching ID was found
                        this.connection.send(new MessageDeliveryStatus(false, "Delivery Failed\n" + cm));
                    } else {
                        // send the msg
                        rec.send(cm);
                    }
                } else {
                    System.out.println("Unrecognized message type " + msg);
                }
            } catch (IOException | ClassNotFoundException | NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException ex) {
                ex.printStackTrace();
                // stop connection
                this.connection.end();
                System.out.println("Connection terminated");
            }
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        connection = null;
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

    Out(ObjectOutputStream out, Connection c) {
        this.out = out;
        this.connection = c;
        this.buffer = new ConcurrentLinkedQueue<>();
        this.running = true;
    }

    @Override
    public void run() {
        System.out.println("Waiting for outgoing messages");
        while (running) {
            Message msg = buffer.poll();
            if (msg == null) {
                continue;
            }
            if (connection.getUser() != null) {
                SecretKey key = connection.getUser().getKey();
                byte[] data = SerializationUtils.serialize(msg);
                try {
                    byte[] encrypted = AESUtils.encrypt(data, key);
                    EncryptedMessage em = new EncryptedMessage(encrypted, true);
                    out.writeObject(em);
                } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | IOException | InvalidAlgorithmParameterException e) {
                    e.printStackTrace();
                    // assuming that the connection is not possible
                    this.connection.end();
                    System.out.println("Connection terminated");
                }
            }
        }
        try {
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        connection = null; // dereference
    }

    /**
     * THE USER MUST NOT BE NULL WHEN THIS METHOD IS CALLED
     */
    void clearInbox() {
        User user = this.connection.getUser();
        assert user != null;
        if (!user.getInbox().isEmpty()) {
            buffer.addAll(user.getInbox());
            user.getInbox().clear();
        }
    }

    void send(@NotNull Message msg) {
        this.buffer.add(msg);
    }

    void stop() {
        running = false;
    }
}