package jmessenger.client;

import jmessenger.shared.EncryptedMessage;
import jmessenger.shared.Message;
import jmessenger.shared.RSAUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.PublicKey;
import java.util.Queue;

public class Out implements Runnable {
    private Queue<Message> buffer;
    private Socket socket;
    private ObjectOutputStream out;
    private boolean running;

    public Out(@NotNull Socket s) throws IOException {
        this.socket = s;
        this.out = new ObjectOutputStream(s.getOutputStream());
    }

    @Override
    public void run() {
        running = true;
        for (; ; ) {
            Message msg = buffer.poll();
            if (msg == null) {
                if (running) {
                    continue;
                } else {
                    end();
                    return;
                }
            }
            // encrypt and send
            PublicKey pub = Messenger.getInstance().getServerPublicKey();
            byte[] unencrypted = SerializationUtils.serialize(msg);
            try {
                byte[] encrypted = RSAUtils.encrypt(unencrypted, pub);
                EncryptedMessage encryptedMsg = new EncryptedMessage(encrypted);
                out.writeObject(encryptedMsg);
            } catch (Exception e) {
                NotificationCenter.getInstance().add(e);
            }
        }
    }

    private void end() {
        try {
            out.flush();
            out.close();
        } catch (IOException e) {
            NotificationCenter.getInstance().add(e);
        }
    }

    public void stop() {
        running = false;
    }

    public void send(@NotNull Message msg) {
        buffer.add(msg);
    }
}
