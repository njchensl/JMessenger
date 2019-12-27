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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

public class UserDatabaseBackup implements Runnable {
    private boolean running;

    UserDatabaseBackup() {
        running = true;
    }

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void run() {
        File db = new File("database");
        if (!db.exists()) {
            try {
                db.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Backup thread terminated!!!\nNew users will NOT be saved");
                return;
            }
        }

        while (running) {
            try {
                Thread.sleep(5000);
                // recreate the file
                db.delete();
                db.createNewFile();
                ObjectOutputStream out;
                try {
                    out = new ObjectOutputStream(new FileOutputStream(db));
                } catch (IOException e) {
                    e.printStackTrace();
                    System.err.println("Backup thread terminated!!!\nNew users will NOT be saved");
                    return;
                }
                // save the user database
                List<User> users = Server.getInstance().getUsers();
                out.writeObject(users);
                out.flush();
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        this.running = false;
    }
}
