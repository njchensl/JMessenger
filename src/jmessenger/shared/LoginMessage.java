package jmessenger.shared;

import org.jetbrains.annotations.NotNull;

import java.security.PublicKey;

public class LoginMessage extends ServerMessage {
    protected PublicKey publicKey;

    public LoginMessage() {
        super();
    }

    public LoginMessage(@NotNull PublicKey publicKey) {
        this();
        this.publicKey = publicKey;
    }

    @NotNull
    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(@NotNull PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    @NotNull
    @Override
    public String toString() {
        return "LOGIN MESSAGE\n" + super.toString() + "\nPublic key: " + RSAUtils.encode(publicKey);
    }
}
