package jmessenger.client;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Messenger {
    private static Messenger messenger;
    private In in;
    private Out out;
    private Socket socket;
    private List<Conversation> conversationList;

    public Messenger(String host, int port) throws IOException {
        this.conversationList = new ArrayList<>();
        this.initialize(host, port);
    }

    @NotNull
    public static Messenger getInstance() {
        return messenger;
    }

    public static void main(String... args) {

    }

    private void initialize(String host, int port) throws IOException {
        // TODO establish a connection
        this.socket = new Socket(host, port);
    }

    public void close() throws IOException {
        socket.close();
    }
}
