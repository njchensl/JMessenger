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

import javax.swing.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
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
    private PrivateKey myPrivateKey;
    private PublicKey myPublicKey;
    private boolean registered;

    public Messenger(@NotNull String host, int port, PublicKey pub, PrivateKey pri, boolean registered) throws IOException {
        this.conversationList = new ArrayList<>();
        this.nc = NotificationCenter.getInstance();
        this.myPublicKey = pub;
        this.myPrivateKey = pri;
        this.registered = registered;
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
            }
        }
        System.out.println("Running");
        // read the files
        File privateKey = new File("private.key");
        File publicKey = new File("public.key");
        ObjectInputStream sPrivate, sPublic;
        PublicKey pub = null;
        PrivateKey pri = null;
        try {
            sPrivate = new ObjectInputStream(new FileInputStream(privateKey));
            sPublic = new ObjectInputStream(new FileInputStream(publicKey));
            pub = (PublicKey) sPublic.readObject();
            pri = (PrivateKey) sPrivate.readObject();
            sPrivate.close();
            sPublic.close();
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, e.getStackTrace(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(3);
        }

        // pub / pri will definitely not be null at this point

        // read the client config
        try {
            Scanner sc = new Scanner(conf);
            boolean registered = Boolean.parseBoolean(sc.nextLine());
            String hostName = sc.nextLine();
            int port = Integer.parseInt(sc.nextLine());
            sc.close();

            messenger = new Messenger(hostName, port, pub, pri, registered);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e.getStackTrace(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(4);
        }

    }

    private static void initialize(@NotNull File conf) throws IOException, NoSuchAlgorithmException {
        System.out.println("Initializing");
        File privateKey = new File("private.key");
        File publicKey = new File("public.key");
        // create the files
        conf.createNewFile();
        privateKey.createNewFile();
        publicKey.createNewFile();
        ObjectOutputStream sPrivate = new ObjectOutputStream(new FileOutputStream(privateKey));
        ObjectOutputStream sPublic = new ObjectOutputStream(new FileOutputStream(publicKey));
        // Generate key pair
        RSAKeyPairGenerator pair = new RSAKeyPairGenerator();
        PublicKey pub = pair.getPublicKey();
        PrivateKey pri = pair.getPrivateKey();
        sPublic.writeObject(pub);
        sPrivate.writeObject(pri);
        sPublic.flush();
        sPrivate.flush();
        sPublic.close();
        sPrivate.close();

        // client configuration
        BufferedWriter bfConf = new BufferedWriter(new FileWriter(conf));
        bfConf.write("false\n"); // this will trigger the registration process
        bfConf.write(JOptionPane.showInputDialog("Please enter the server hostname:") + "\n");
        bfConf.write(JOptionPane.showInputDialog("Please enter the port") + "\n");
        bfConf.flush();
        bfConf.close();
    }

    public void send(@NotNull Message msg) {
        this.out.send(msg);
    }

    private void initialize(@NotNull String host, int port) throws IOException {
        this.socket = new Socket();
        this.socket.connect(new InetSocketAddress(host, port), 10000);
        this.in = new In(socket);
        this.out = new Out(socket);
        new Thread(in).start();
        new Thread(out).start();

        if (!registered) {
            register();
        }

        // TODO show GUI
    }

    @NotNull
    public PublicKey getServerPublicKey() {
        return serverPublicKey;
    }

    public void setServerPublicKey(@NotNull PublicKey serverPublicKey) {
        this.serverPublicKey = serverPublicKey;
    }

    @NotNull
    public PrivateKey getMyPrivateKey() {
        return myPrivateKey;
    }

    public void setMyPrivateKey(@NotNull PrivateKey myPrivateKey) {
        this.myPrivateKey = myPrivateKey;
    }

    @NotNull
    public PublicKey getMyPublicKey() {
        return myPublicKey;
    }

    public void setMyPublicKey(@NotNull PublicKey myPublicKey) {
        this.myPublicKey = myPublicKey;
    }

    private void register() {
        // register with the server
        RegistrationMessage msg = new RegistrationMessage(myPublicKey);
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
            bfConf.write("true");
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
                RegistrationResponseMessage rrm = (RegistrationResponseMessage) msg;
                JOptionPane.showMessageDialog(null, "You have successfully registered with the server\nUser ID: " + rrm.getUserID(), "Registration Successful", JOptionPane.INFORMATION_MESSAGE);
                finishRegistration();
            }
        }
    }
}
