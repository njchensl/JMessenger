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
                ObjectOutputStream out = null;
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
