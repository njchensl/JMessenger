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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

public class ConversationSaver implements Runnable {
    private final List<Conversation> conversations;
    private volatile boolean working;

    public ConversationSaver(List<Conversation> conversations) {
        this.conversations = conversations;
        this.working = false;
    }

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void run() {
        working = true;
        File con = new File("clientconfig/conversations");
        // create new file if does not exist
        if (!con.exists()) {
            try {
                con.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Backup thread terminated!!!\nNew users will NOT be saved");
                return;
            }
        }

        for (; ; ) {
            try {
                working = false;
                Thread.sleep(5000);
                working = true;
                // recreate the file
                con.delete();
                con.createNewFile();
                ObjectOutputStream out;

                try {
                    out = new ObjectOutputStream(new FileOutputStream(con));
                } catch (IOException e) {
                    e.printStackTrace();
                    System.err.println("Conversation list saving thread terminated!!!\nNew conversations will NOT be saved");
                    return;
                }
                // save the conversation list
                synchronized (conversations) {
                    out.writeObject(conversations);
                    out.flush();
                }
            } catch (InterruptedException | IOException e) {
                NotificationCenter.getInstance().add(e);
            }
        }
    }

    /**
     * this method is used internally to determine if this thread is still running
     *
     * @return whether or not it is doing work
     */
    boolean isWorking() {
        return working;
    }
}
