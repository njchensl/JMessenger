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

import jmessenger.shared.AESUtils;
import jmessenger.shared.EncryptedMessage;
import jmessenger.shared.Message;
import org.apache.commons.lang3.SerializationUtils;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

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
                // ALWAYS USING AES
                EncryptedMessage msg = (EncryptedMessage) in.readObject();
                byte[] data = msg.getMessage();
                // decryption
                SecretKey key = Messenger.getInstance().getMyKey();
                byte[] decrypted = AESUtils.decrypt(data, key);
                Message decryptedMsg = SerializationUtils.deserialize(decrypted);
                System.out.println("Message Received:");
                System.out.println(decryptedMsg);
                Messenger.getInstance().receive(decryptedMsg);
            } catch (Exception e) {
                NotificationCenter.getInstance().add(e);
            }
        }
    }

    public void stop() {
        running = false;
    }
}
