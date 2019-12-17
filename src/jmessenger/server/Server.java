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

import jmessenger.shared.RSAKeyPairGenerator;
import jmessenger.shared.RSAUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.crypto.SecretKey;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author frche1699
 */
public class Server implements Runnable {
    private static Server server;
    private ServerSocket ss;
    private List<@NotNull User> users;
    private List<@NotNull Connection> connections;
    private PrivateKey privateKey;
    private UserDatabaseBackup backup;

    /**
     * no server instance for anyone else
     */
    @SuppressWarnings("unchecked")
    private Server(PrivateKey privateKey, int port) throws IOException, ClassNotFoundException {
        this.privateKey = privateKey;
        this.users = new ArrayList<>();
        // read users from database
        File db = new File("database");
        if (db.exists()) {
            Object o = new ObjectInputStream(new FileInputStream(db)).readObject();
            this.users = (List<User>) o;
            for (User u : users) {
                u.resetUserInboxAndConnection();
            }
        }

        this.connections = new ArrayList<>();
        this.ss = new ServerSocket(port);
        System.out.println("Server started on port " + port);
        backup = new UserDatabaseBackup();
        Thread t = new Thread(backup);
        t.setDaemon(true);
        t.start();
    }

    public static void main(String... args) {
        File conf = new File("server.conf");
        if (!conf.exists()) {
            System.out.println("Initializing");
            try {
                initialize(conf);
            } catch (IOException | NoSuchAlgorithmException | NoSuchProviderException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        // read the server private key and port information
        int port = 8966;
        try {
            Scanner sc = new Scanner(conf);
            port = Integer.parseInt(sc.nextLine());
            sc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(2);
        }
        PrivateKey pri = null;
        try {
            ObjectInputStream privateIn = new ObjectInputStream(new FileInputStream(new File("server-private.key")));
            pri = (PrivateKey) privateIn.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(3);
        }

        try {
            server = new Server(pri, port);
            new Thread(server).start();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(4);
        }

    }

    public static Server getInstance() {
        return server;
    }

    private static void initialize(@NotNull File conf) throws IOException, NoSuchAlgorithmException, NoSuchProviderException {
        File privateKey = new File("server-private.key");
        File publicKey = new File("server-public.key");
        conf.createNewFile();
        privateKey.createNewFile();
        publicKey.createNewFile();
        // generate RSA keypair
        RSAKeyPairGenerator rsakpg = new RSAKeyPairGenerator();
        PrivateKey pri = rsakpg.getPrivateKey();
        PublicKey pub = rsakpg.getPublicKey();
        ObjectOutputStream sPrivate = new ObjectOutputStream(new FileOutputStream(privateKey));
        ObjectOutputStream sPublic = new ObjectOutputStream(new FileOutputStream(publicKey));
        sPrivate.writeObject(pri);
        sPublic.writeObject(pub);
        sPrivate.flush();
        sPublic.flush();
        sPrivate.close();
        sPublic.close();
        // show the user the base64 string representation of the public key
        System.out.println("Public Key:\n");
        System.out.println(RSAUtils.encode(pub));

        Scanner in = new Scanner(System.in);
        System.out.println("Which port do you want to run the server on?");
        String port = in.nextLine();
        BufferedWriter bw = new BufferedWriter(new FileWriter(conf));
        bw.write(port + "\n");
        bw.flush();
        bw.close();
    }

    @Override
    public void run() {
        for (; ; ) {
            System.out.println("Listening for incoming connections...");
            try {
                Socket s = ss.accept();
                System.out.println("User connected: " + s.getRemoteSocketAddress());
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
    User getUserByKey(@NotNull SecretKey key) {
        for (User u : users) {
            System.out.println(RSAUtils.encode(u.getKey()));
            System.out.println(RSAUtils.encode(key));
            if (RSAUtils.encode(u.getKey()).equals(RSAUtils.encode(key))) {
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

    List<User> getUsers() {
        return this.users;
    }

    void addUser(@NotNull User u) {
        this.users.add(u);
    }

    List<Connection> getConnections() {
        return connections;
    }
}
