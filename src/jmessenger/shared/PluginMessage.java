package jmessenger.shared;

import org.jetbrains.annotations.NotNull;

public class PluginMessage extends ClientMessage {
    protected String type;
    protected byte[] data;

    public PluginMessage(int id, byte[] data, String type) {
        super(id);
        this.data = data;
        this.type = type;
    }

    public byte[] getData() {
        return data;
    }

    @NotNull
    @Override
    @SuppressWarnings("ImplicitArrayToString")
    public String toString() {
        return "PLUGIN MESSAGE\n" + super.toString() + "\nData: " + data.toString();
    }

    public String getType() {
        return type;
    }
}
