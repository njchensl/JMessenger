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

import jmessenger.shared.RSAUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

/**
 * @author frche1699
 */
public class Server implements Runnable {
    private static Server server;
    private ServerSocket ss;
    private List<@NotNull User> users;
    private List<@NotNull Connection> connections;
    private PrivateKey privateKey;

    Server(PrivateKey privateKey, int port) throws IOException {
        this.privateKey = privateKey;
        this.users = new ArrayList<>();
        this.connections = new ArrayList<>();
        this.ss = new ServerSocket(port);
    }

    public static void main(String... args) {

    }

    public static Server getInstance() {
        return server;
    }

    // TODO
    private static void initialize() {

    }

    @Override
    public void run() {
        for (; ; ) {
            try {
                Socket s = ss.accept();
                connections.add(new Connection(s));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    PrivateKey getPrivateKey() {
        return privateKey;
    }

    void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    @Nullable
    User getUserByPublicKey(@NotNull PublicKey publicKey) {
        for (User u : users) {
            if (RSAUtils.encode(u.getPublicKey()).equals(RSAUtils.encode(publicKey))) {
                return u;
            }
        }
        return null;
    }

    @Nullable
    User getUserByID(int id) {
        for (User u : users) {
            if (u.getId() == id) {
                return u;
            }
        }
        return null;
    }

    void addUser(@NotNull User u) {
        this.users.add(u);
    }

    List<Connection> getConnections() {
        return connections;
    }
}
