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

import com.formdev.flatlaf.FlatIntelliJLaf;
import jmessenger.client.ui.MainFrame;
import jmessenger.client.ui.MainPanel;
import jmessenger.client.ui.MessagesPanel;
import jmessenger.shared.*;
import mdlaf.MaterialLookAndFeel;
import org.jetbrains.annotations.NotNull;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.util.List;
import java.util.*;

@SuppressWarnings("WeakerAccess")
public class Messenger {
    public final static String version = "1.1";
    private static volatile Messenger messenger; // made volatile to suppress a warning

    private In in;
    private Out out;
    private Socket socket;
    private List<Conversation> conversationList;
    private NotificationCenter nc;
    private PublicKey serverPublicKey;
    private SecretKey myKey;
    private boolean registered;
    private boolean usingAES;
    private MainFrame mainFrame;
    private ConversationSaver conversationSaver;

    /**
     * creates a messenger object
     *
     * @param host            the host name
     * @param port            the port
     * @param myKey           my secret key
     * @param serverPublicKey the server's public key
     * @param registered      if it has been registered
     * @throws IOException io exception
     */
    private Messenger(@NotNull String host, int port, SecretKey myKey, PublicKey serverPublicKey, boolean registered) throws IOException, ClassNotFoundException {
        this.conversationList = new ArrayList<>();
        this.nc = NotificationCenter.getInstance();
        this.serverPublicKey = serverPublicKey;
        this.registered = registered;
        this.myKey = myKey;
        this.usingAES = false;
        this.initialize(host, port);
    }

    /**
     * @return the instance of Messenger, will never be null
     */
    @NotNull
    public static Messenger getInstance() {
        while (messenger == null) {
            Thread.onSpinWait();
        }
        return messenger;
    }

    public static void main(String... args) {
        // show splash screen
        SplashScreen ss = new SplashScreen();
        ss.show();

        boolean safe = false;
        if (args.length == 1 && args[0].replaceAll("-", "").equals("safemode")) {
            safe = true;
        }
        // L&F
        try {
            UIManager.setLookAndFeel(new FlatIntelliJLaf()/*new MaterialLookAndFeel()*/);
        } catch (Throwable t) {
            NotificationCenter.getInstance().add(t);
        }

        if (UIManager.getLookAndFeel() instanceof MaterialLookAndFeel) {
            UIManager.put("OptionPane.messageFont", new Font("Arial", Font.PLAIN, 11));
            UIManager.put("OptionPane.buttonFont", new Font("Arial", Font.PLAIN, 11));
        }

        File conf = new File("clientconfig/messenger.conf");
        if (!conf.exists()) {
            try {
                initialize(conf);
            } catch (IOException | NoSuchAlgorithmException | NoSuchProviderException e) {
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                String exceptionAsString = sw.toString();
                // show throwable and stack trace
                JOptionPane.showMessageDialog(null, new JScrollPane(new JTextArea() {{
                    setEditable(false);
                    setText("Unable to initialize\n" + exceptionAsString);
                }}), "Fatal Error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        }
        //System.out.println("Running");
        // read the keys

        SecretKey key = null;
        File k = new File("clientconfig/client.key");
        try {
            ObjectInputStream keyIn = new ObjectInputStream(new FileInputStream(k));
            key = (SecretKey) keyIn.readObject();
            keyIn.close();
        } catch (IOException | ClassNotFoundException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            // show throwable and stack trace
            JOptionPane.showMessageDialog(null, new JScrollPane(new JTextArea() {{
                setEditable(false);
                setText("Unable to read client key\n" + exceptionAsString);
            }}), "Fatal Error", JOptionPane.ERROR_MESSAGE);
            System.exit(2);
        }

        // read the client config
        try {
            Scanner sc = new Scanner(conf);
            boolean registered = Boolean.parseBoolean(sc.nextLine());
            String hostName = sc.nextLine();
            int port = Integer.parseInt(sc.nextLine());
            sc.close();

            // read the server public key
            ObjectInputStream serverPublicKeyIn = new ObjectInputStream(new FileInputStream(new File("clientconfig/client-stored-server-public.key")));
            PublicKey serverPublicKey = (PublicKey) serverPublicKeyIn.readObject();
            serverPublicKeyIn.close();

            messenger = new Messenger(hostName, port, key, serverPublicKey, registered);

            // load plugins
            PluginManager.initialize();
            PluginManager pm = PluginManager.getInstance();
            assert pm != null;

            // if safe mode, don't load plugins
            if (!safe) {
                pm.loadPlugins();
            }

            pm.onStart(); // call the onStart method of each plugin

            messenger.displayGUI();

            // hide splash screen
            ss.hide();

        } catch (IOException | ClassNotFoundException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            // show throwable and stack trace
            JOptionPane.showMessageDialog(null, new JScrollPane(new JTextArea() {{
                setEditable(false);
                setText("Unable to start the messenger\n" + exceptionAsString);
            }}), "Fatal Error", JOptionPane.ERROR_MESSAGE);
            System.exit(3);
        } catch (MissingResourceException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            // show throwable and stack trace
            JOptionPane.showMessageDialog(null, new JScrollPane(new JTextArea() {{
                setEditable(false);
                setText("Error loading resources\nPlease check your language pack installation\n" + exceptionAsString);
            }}), "Fatal Error", JOptionPane.ERROR_MESSAGE);
            System.exit(10);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void initialize(@NotNull File conf) throws IOException, NoSuchAlgorithmException, NoSuchProviderException {
        //System.out.println("Initializing");

        // create the files
        conf.createNewFile();

        File clientKey = new File("clientconfig/client.key");
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
        File serverPublicKeyFile = new File("clientconfig/client-stored-server-public.key");
        serverPublicKeyFile.createNewFile();
        ObjectOutputStream outServerPublic = new ObjectOutputStream(new FileOutputStream(serverPublicKeyFile));
        outServerPublic.writeObject(serverPub);
        outServerPublic.flush();
        outServerPublic.close();
        bfConf.flush();
        bfConf.close();
    }

    private void displayGUI() {
        this.mainFrame = new MainFrame();
        SwingUtilities.invokeLater(() -> this.mainFrame.setVisible(true));
    }

    /**
     * stops the messenger by closing the IO streams and then the socket
     */
    public void close() throws IOException {
        Objects.requireNonNull(PluginManager.getInstance()).onClose();
        this.out.stop();
        this.in.stop();
        while (!in.isTerminated() || !out.isTerminated()) {
            Thread.onSpinWait();
        }
        // prevents data corruption
        while (conversationSaver.isWorking()) {
            Thread.onSpinWait();
        }
        this.socket.close();
    }

    /**
     * @return whether or not the encryption mode is set to AES
     */
    public boolean isUsingAES() {
        return usingAES;
    }

    /**
     * set the encryption mode
     * true: AES
     * false: RSA
     *
     * @param b the mode
     */
    public void setUsingAES(boolean b) {
        this.usingAES = b;
    }

    /**
     * sends out a message
     *
     * @param msg the message to send
     */
    public synchronized void send(@NotNull Message msg) {
        this.out.send(msg);
        this.sortConversations();
        //PluginManager.getInstance().onMessageSent(msg);
    }

    @SuppressWarnings("unchecked")
    private void initialize(@NotNull String host, int port) throws IOException, ClassNotFoundException {
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

        // read all conversations from the datafile
        File db = new File("clientconfig/conversations");
        if (db.exists()) {
            Object o = new ObjectInputStream(new FileInputStream(db)).readObject();
            this.conversationList = (List<Conversation>) o;
        }

        // start to backup conversations to the datafile
        this.conversationSaver = new ConversationSaver(this.conversationList);
        Thread t = new Thread(conversationSaver);
        t.setDaemon(true);
        t.start();
    }

    /**
     * @return the server public key
     */
    @NotNull
    public PublicKey getServerPublicKey() {
        return serverPublicKey;
    }

    /**
     * @return my AES secret key
     */
    public SecretKey getMyKey() {
        return this.myKey;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void register() {
        int result = JOptionPane.showConfirmDialog(null, "Would you like to link this client to an existing account?", "Registration", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            // ask for the current key
            String keyString = JOptionPane.showInputDialog("Please enter your secret key");
            byte[] encodedKey = Base64.getDecoder().decode(keyString);
            SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
            // write to file
            File keyFile = new File("clientconfig/client.key");
            if (keyFile.exists()) {
                keyFile.delete();
            }
            try {
                keyFile.createNewFile();
                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(keyFile));
                oos.writeObject(key);
                oos.flush();
                oos.close();
                this.myKey = key;
                finishRegistration();
            } catch (IOException e) {
                nc.add(e);
                // if things go wrong, register with the server
                registerWithServer();
            }
        } else {
            registerWithServer();
        }
    }

    private void registerWithServer() {
        // register with the server
        RegistrationMessage msg = new RegistrationMessage(myKey);
        out.send(msg);
    }

    private void finishRegistration() {
        registered = true;
        // write it to the file
        File f = new File("clientconfig/messenger.conf");
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

    /**
     * receive and analyse the message
     *
     * @param msg the message to analyse
     */
    @SuppressWarnings("SynchronizeOnNonFinalField")
    public synchronized void receive(@NotNull Message msg) {
        // analyse the message type
        if (msg instanceof ClientMessage) {
            ClientMessage cm = (ClientMessage) msg;
            cm.setMyMessage(false); // mark the message as sent by another person
            int recipient = cm.getRecipientID();
            boolean added = false;
            synchronized (conversationList) {
                for (Conversation c : getConversationList()) {
                    if (c.getRecipient() == recipient) {
                        c.addMessage(cm); // the rest is handled by the GUI message renderer
                        added = true;
                    }
                }
            }
            // if none was found, create a new one
            if (!added) {
                Conversation cNew = new Conversation(recipient);
                cNew.addMessage(cm);
                synchronized (conversationList) {
                    getConversationList().add(0, cNew);
                }
            }
            // update the gui
            try {
                JPanel pnl = ((MainPanel) this.getMainFrame().getContentPane()).getMainPanel();
                if (pnl instanceof MessagesPanel) {
                    ((MessagesPanel) pnl).getConversationListPanel().updateConversations();
                }
                // sort the conversations
                sortConversations();
                this.getMainFrame().revalidate();
            } catch (NullPointerException ignored) {
                // the exception will be thrown if the user is saved messages from the server
            }
        } else {
            // server message
            // analyse the type
            if (msg instanceof MessageDeliveryStatus) {
                MessageDeliveryStatus mds = (MessageDeliveryStatus) msg;
                if (!mds.isSuccessful()) {
                    // inform the user about the failure
                    JOptionPane.showMessageDialog(null, "Message Delivery Failed\n" + msg);
                    nc.add(new Exception("Message Delivery Failed\n" + mds.getMessage()));
                }
            } else if (msg instanceof RegistrationResponseMessage) {
                this.usingAES = true; // when registered, the server knows the client AES key
                RegistrationResponseMessage rrm = (RegistrationResponseMessage) msg;
                JOptionPane.showMessageDialog(null, "You have successfully registered with the server\nUser ID: " + rrm.getUserID(), "Registration Successful", JOptionPane.INFORMATION_MESSAGE);
                JOptionPane.showMessageDialog(null, new JTextArea(RSAUtils.encode(myKey)), "Secret Key", JOptionPane.INFORMATION_MESSAGE);
                finishRegistration();
            }
        }
        Objects.requireNonNull(PluginManager.getInstance()).onMessageReceived(msg);
    }

    /**
     * @return the main JFrame
     */
    public MainFrame getMainFrame() {
        return mainFrame;
    }

    /**
     * @return the conversationList
     */
    public List<Conversation> getConversationList() {
        return conversationList;
    }

    /**
     * checks if a conversation already exists
     *
     * @param recipient the recipient ID
     * @return if it exists
     */
    @SuppressWarnings("SynchronizeOnNonFinalField")
    public boolean conversationAlreadyExists(int recipient) {
        synchronized (conversationList) {
            for (Conversation co : conversationList) {
                if (recipient == co.getRecipient()) {
                    return true;
                }
            }
        }
        return false;
    }

    @SuppressWarnings("SynchronizeOnNonFinalField")
    private void sortConversations() {
        synchronized (conversationList) {
            sort(conversationList, 0, conversationList.size() - 1);
        }
    }

    private int partition(List<Conversation> arr, int low, int high) {
        Conversation pivot = arr.get(high);
        int i = (low - 1);
        for (int j = low; j < high; j++) {
            if (arr.get(j).getTime() > pivot.getTime()) {
                i++;
                Conversation temp = arr.get(i);
                arr.set(i, arr.get(j));
                arr.set(j, temp);
            }
        }

        Conversation temp = arr.get(i + 1);
        arr.set(i + 1, arr.get(high));
        arr.set(high, temp);

        return i + 1;
    }


    private void sort(List<Conversation> arr, int low, int high) {
        if (low < high) {
            int pi = partition(arr, low, high);
            sort(arr, low, pi - 1);
            sort(arr, pi + 1, high);
        }
    }

    public In getIn() {
        return in;
    }

    public Out getOut() {
        return out;
    }
}
