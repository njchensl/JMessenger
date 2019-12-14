package jmessenger.client;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class NotificationCenter {
    private static NotificationCenter nc;

    static {
        nc = new NotificationCenter();
    }

    private List<@NotNull Throwable> throwableList;

    public NotificationCenter() {
        this.throwableList = new ArrayList<>();
    }

    public static NotificationCenter getInstance() {
        return nc;
    }

    public void add(@NotNull Throwable t) {
        t.printStackTrace();
        throwableList.add(t);
    }

    public @NotNull List<@NotNull Throwable> getThrowables() {
        return this.throwableList;
    }
}
