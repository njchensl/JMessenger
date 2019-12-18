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
import org.apache.commons.lang3.SerializationUtils;
import org.jetbrains.annotations.NotNull;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class Out implements Runnable {
    private Queue<Message> buffer;
    private Socket socket;
    private ObjectOutputStream out;
    private boolean running;

    public Out(@NotNull Socket s) throws IOException {
        this.socket = s;
        this.out = new ObjectOutputStream(s.getOutputStream());
        this.buffer = new ArrayBlockingQueue<>(50);
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
            byte[] unencrypted = SerializationUtils.serialize(msg);
            System.out.println(unencrypted.length);
            // encrypt and send
            if (Messenger.getInstance().isUsingAES()) {
                SecretKey key = Messenger.getInstance().getMyKey();
                try {
                    byte[] encrypted = AESUtils.encrypt(unencrypted, key);
                    EncryptedMessage encryptedMsg = new EncryptedMessage(encrypted, true);
                    out.writeObject(encryptedMsg);
                } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | IOException | InvalidAlgorithmParameterException e) {
                    NotificationCenter.getInstance().add(e);
                }
            } else {
                if (msg instanceof LoginMessage) {
                    Messenger.getInstance().setUsingAES(true);
                }
                PublicKey pub = Messenger.getInstance().getServerPublicKey();
                try {
                    byte[] encrypted = RSAUtils.encrypt(unencrypted, pub);
                    EncryptedMessage encryptedMsg = new EncryptedMessage(encrypted, false);
                    out.writeObject(encryptedMsg);
                } catch (Exception e) {
                    NotificationCenter.getInstance().add(e);
                }
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

    public synchronized void send(@NotNull Message msg) {
        buffer.add(msg);
    }
}
