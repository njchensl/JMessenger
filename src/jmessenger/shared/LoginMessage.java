package jmessenger.shared;

import org.jetbrains.annotations.NotNull;

import javax.crypto.SecretKey;

public class LoginMessage extends ServerMessage {
    protected SecretKey key;

    public LoginMessage(@NotNull SecretKey key) {
        super();
        this.key = key;
    }

    @NotNull
    public SecretKey getKey() {
        return key;
    }

    @NotNull
    @Override
    public String toString() {
        return "LOGIN MESSAGE\n" + super.toString() + "\nKey: " + RSAUtils.encode(key);
    }
}
