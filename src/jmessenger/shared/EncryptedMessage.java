package jmessenger.shared;

import org.jetbrains.annotations.NotNull;

public class EncryptedMessage implements java.io.Serializable {
    private byte[] data;
    private boolean usingAES;

    public EncryptedMessage(@NotNull byte[] data, boolean usingAES) {
        this.data = data.clone();
        this.usingAES = usingAES;
    }

    @NotNull
    public byte[] getMessage() {
        return this.data;
    }

    public boolean isUsingAES() {
        return usingAES;
    }
}
