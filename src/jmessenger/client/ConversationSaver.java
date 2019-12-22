package jmessenger.client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

public class ConversationSaver implements Runnable {
    private final List<Conversation> conversations;

    public ConversationSaver(List<Conversation> conversations) {
        this.conversations = conversations;
    }

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void run() {
        File con = new File("conversations");
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

                Thread.sleep(5000);
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
                e.printStackTrace();
            }
        }
    }
}
