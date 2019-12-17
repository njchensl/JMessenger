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

import jmessenger.shared.*;
import org.jetbrains.annotations.NotNull;

import javax.crypto.SecretKey;
import javax.swing.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Messenger {
    private static Messenger messenger;
    private In in;
    private Out out;
    private Socket socket;
    private List<Conversation> conversationList;
    private NotificationCenter nc;
    private PublicKey serverPublicKey;
    private SecretKey myKey;
    private boolean registered;
    private boolean usingAES;

    public Messenger(@NotNull String host, int port, SecretKey myKey, PublicKey serverPublicKey, boolean registered) throws IOException {
        this.conversationList = new ArrayList<>();
        this.nc = NotificationCenter.getInstance();
        this.serverPublicKey = serverPublicKey;
        this.registered = registered;
        this.myKey = myKey;
        this.usingAES = false;
        this.initialize(host, port);
    }

    @NotNull
    public static Messenger getInstance() {
        return messenger;
    }

    public void close() throws IOException {
        socket.close();
    }

    public static void main(String... args) {
        File conf = new File("messenger.conf");
        if (!conf.exists()) {
            try {
                initialize(conf);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, e.getStackTrace(), "Error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            } catch (NoSuchAlgorithmException e) {
                JOptionPane.showMessageDialog(null, e.getStackTrace(), "Error", JOptionPane.ERROR_MESSAGE);
                System.exit(2);
            } catch (NoSuchProviderException e) {
                e.printStackTrace();
                System.exit(4);
            }
        }
        System.out.println("Running");
        // read the keys

        SecretKey key = null;
        File k = new File("client.key");
        try {
            ObjectInputStream keyIn = new ObjectInputStream(new FileInputStream(k));
            key = (SecretKey) keyIn.readObject();
            keyIn.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(3);
        }

        // read the client config
        try {
            Scanner sc = new Scanner(conf);
            boolean registered = Boolean.parseBoolean(sc.nextLine());
            String hostName = sc.nextLine();
            int port = Integer.parseInt(sc.nextLine());
            sc.close();

            // read the server public key
            ObjectInputStream serverPublicKeyIn = new ObjectInputStream(new FileInputStream(new File("client-stored-server-public.key")));
            PublicKey serverPublicKey = (PublicKey) serverPublicKeyIn.readObject();
            serverPublicKeyIn.close();

            messenger = new Messenger(hostName, port, key, serverPublicKey, registered);
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, e.getStackTrace(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(4);
        }

    }

    private static void initialize(@NotNull File conf) throws IOException, NoSuchAlgorithmException, NoSuchProviderException {
        System.out.println("Initializing");

        // create the files
        conf.createNewFile();

        File clientKey = new File("client.key");
        ObjectOutputStream sKey = new ObjectOutputStream(new FileOutputStream(clientKey));

        // generate client key
        SecretKey key = AESUtils.generate();
        sKey.writeObject(key);
        sKey.flush();
        sKey.close();

        // client configuration
        BufferedWriter bfConf = new BufferedWriter(new FileWriter(conf));
        bfConf.write("false\n"); // this will trigger the registration process
        bfConf.write(JOptionPane.showInputDialog("Please enter the server hostname:") + "\n");
        bfConf.write(JOptionPane.showInputDialog("Please enter the port") + "\n");
        String serverPublicKey = JOptionPane.showInputDialog("Please enter the base64 string representation of the server's public key");

        // ask and save the server public key
        assert serverPublicKey != null;
        PublicKey serverPub = RSAUtils.getPublicKey(serverPublicKey);
        assert serverPub != null;
        // store the server public key
        File serverPublicKeyFile = new File("client-stored-server-public.key");
        serverPublicKeyFile.createNewFile();
        ObjectOutputStream outServerPublic = new ObjectOutputStream(new FileOutputStream(serverPublicKeyFile));
        outServerPublic.writeObject(serverPub);
        outServerPublic.flush();
        outServerPublic.close();
        bfConf.flush();
        bfConf.close();
    }

    public boolean isUsingAES() {
        return usingAES;
    }

    public void setUsingAES(boolean b) {
        this.usingAES = b;
    }

    public void send(@NotNull Message msg) {
        this.out.send(msg);
    }

    private void initialize(@NotNull String host, int port) throws IOException {
        this.socket = new Socket();
        this.socket.connect(new InetSocketAddress(host, port), 10000);

        this.out = new Out(socket);
        this.in = new In(socket);

        new Thread(in).start();
        new Thread(out).start();

        if (!registered) {
            register();
        }

        // login
        LoginMessage lm = new LoginMessage(this.myKey);
        this.send(lm);

        // TODO show GUI
    }

    @NotNull
    public PublicKey getServerPublicKey() {
        return serverPublicKey;
    }

    public SecretKey getMyKey() {
        return this.myKey;
    }

    private void register() {
        // register with the server
        RegistrationMessage msg = new RegistrationMessage(myKey);
        out.send(msg);
    }

    private void finishRegistration() {
        registered = true;
        // write it to the file
        File f = new File("messenger.conf");
        try {
            Scanner sc = new Scanner(f);
            sc.nextLine();
            String data = sc.nextLine() + "\n" + sc.nextLine() + "\n";
            sc.close();
            BufferedWriter bfConf = new BufferedWriter(new FileWriter(f));
            bfConf.write("true\n");
            bfConf.write(data);
            bfConf.flush();
            bfConf.close();
        } catch (IOException e) {
            nc.add(e);
        }
    }

    public synchronized void receive(@NotNull Message msg) {
        // analyse the message type
        if (msg instanceof ClientMessage) {
            ClientMessage cm = (ClientMessage) msg;
            int recipient = cm.getRecipientID();
            boolean added = false;
            for (Conversation c : conversationList) {
                if (c.getRecipient() == recipient) {
                    c.addMessage(cm); // the rest is handled by the GUI message renderer
                    added = true;
                }
            }
            // if none was found, create a new one
            if (!added) {
                Conversation cNew = new Conversation(recipient);
                cNew.addMessage(cm);
                conversationList.add(cNew);
            }
        } else {
            // server message
            // analyse the type
            if (msg instanceof MessageDeliveryStatus) {
                MessageDeliveryStatus mds = (MessageDeliveryStatus) msg;
                if (!mds.isSuccessful()) {
                    nc.add(new Exception("Message Delivery Failed\n" + mds.getMessage()));
                }
            } else if (msg instanceof RegistrationResponseMessage) {
                this.usingAES = true;
                RegistrationResponseMessage rrm = (RegistrationResponseMessage) msg;
                JOptionPane.showMessageDialog(null, "You have successfully registered with the server\nUser ID: " + rrm.getUserID(), "Registration Successful", JOptionPane.INFORMATION_MESSAGE);
                finishRegistration();

            }
        }
    }
}
