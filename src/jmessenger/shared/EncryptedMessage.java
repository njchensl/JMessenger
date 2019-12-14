package jmessenger.shared;

import org.jetbrains.annotations.NotNull;

public class EncryptedMessage implements java.io.Serializable {
    private byte[] data;

    public EncryptedMessage(@NotNull byte[] data) {
        this.data = data.clone();
    }

    @NotNull
    public byte[] getMessage() {
        return this.data;
    }

    public void setMessage(@NotNull byte[] data) {
        this.data = data.clone();
    }
}
