package jmessenger.client;

import jmessenger.shared.Message;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class Messenger {
    private static Messenger messenger;
    private In in;
    private Out out;
    private Socket socket;
    private List<Conversation> conversationList;
    private NotificationCenter nc;
    private PublicKey serverPublicKey;
    private PrivateKey myPrivateKey;
    private PublicKey myPublicKey;

    public Messenger(@NotNull String host, int port) throws IOException {
        this.conversationList = new ArrayList<>();
        this.nc = NotificationCenter.getInstance();
        this.initialize(host, port);
    }

    @NotNull
    public static Messenger getInstance() {
        return messenger;
    }

    public void close() throws IOException {
        socket.close();
    }

    public static void main(String... args) {

    }

    private void initialize(@NotNull String host, int port) throws IOException {
        this.socket = new Socket();
        this.socket.connect(new InetSocketAddress(host, port), 10000);
        this.in = new In(socket);
        this.out = new Out(socket);
    }

    public void send(@NotNull Message msg) {
        this.out.send(msg);
    }

    public void receive(@NotNull Message msg) {

    }

    @NotNull
    public PublicKey getServerPublicKey() {
        return serverPublicKey;
    }

    public void setServerPublicKey(@NotNull PublicKey serverPublicKey) {
        this.serverPublicKey = serverPublicKey;
    }

    @NotNull
    public PrivateKey getMyPrivateKey() {
        return myPrivateKey;
    }

    public void setMyPrivateKey(@NotNull PrivateKey myPrivateKey) {
        this.myPrivateKey = myPrivateKey;
    }

    @NotNull
    public PublicKey getMyPublicKey() {
        return myPublicKey;
    }

    public void setMyPublicKey(@NotNull PublicKey myPublicKey) {
        this.myPublicKey = myPublicKey;
    }

}
