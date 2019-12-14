package jmessenger.client;

import jmessenger.shared.EncryptedMessage;
import jmessenger.shared.Message;
import jmessenger.shared.RSAUtils;
import org.apache.commons.lang3.SerializationUtils;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.security.PrivateKey;

public class In implements Runnable {
    private Socket socket;
    private ObjectInputStream in;
    private boolean running;

    public In(Socket s) throws IOException {
        this.socket = s;
        this.in = new ObjectInputStream(socket.getInputStream());
    }

    @Override
    public void run() {
        running = true;
        while (running) {
            try {
                EncryptedMessage msg = (EncryptedMessage) in.readObject();
                byte[] data = msg.getMessage();
                // decryption
                PrivateKey pri = Messenger.getInstance().getMyPrivateKey();
                byte[] decrypted = RSAUtils.decrypt(data, pri);
                Message decryptedMsg = SerializationUtils.deserialize(decrypted);
                Messenger.getInstance().receive(decryptedMsg);
            } catch (Exception e) {
                NotificationCenter.getInstance().add(e);
            } catch (Error e) {
                JOptionPane.showMessageDialog(null, e, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void stop() {
        running = false;
    }
}
