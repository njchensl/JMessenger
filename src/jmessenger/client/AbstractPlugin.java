package jmessenger.client;

import jmessenger.shared.Message;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractPlugin {
    protected Messenger messenger;

    public AbstractPlugin() {
        messenger = null;
    }

    @Nullable
    public final Messenger getMessenger() {
        return this.messenger;
    }

    public final void setMessenger(@NotNull Messenger messenger) {
        this.messenger = messenger;
    }

    public abstract void onStart();

    public abstract void onClose();

    public abstract void onMessageReceived(@NotNull Message msg);

    public abstract void onMessageSent(@NotNull Message msg);

}
