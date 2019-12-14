package jmessenger.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class In implements Runnable {
    private Socket socket;
    private ObjectInputStream in;

    public In(Socket s) throws IOException {
        this.socket = s;
        this.in = new ObjectInputStream(socket.getInputStream());
    }

    @Override
    public void run() {
        for (; ; ) {
            
        }
    }
}
