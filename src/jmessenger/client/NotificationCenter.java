/*
 * The MIT License
 *
 * Copyright 2019 frche1699.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package jmessenger.client;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class NotificationCenter {
    private static NotificationCenter nc;

    static {
        nc = new NotificationCenter();
    }

    private List<@NotNull Throwable> throwableList;

    /**
     * NO INSTANCE FOR YOU
     */
    private NotificationCenter() {
        this.throwableList = new ArrayList<>();
    }

    /**
     * @return an instance of NC
     */
    public static NotificationCenter getInstance() {
        return nc;
    }

    /**
     * add a new throwable item to the notification center
     *
     * @param t the item to add
     */
    public synchronized void add(@NotNull Throwable t) {
        throwableList.add(0, t);
        // prevents the option pane from showing up if the messenger is closing normally
        if (t instanceof IOException && Messenger.getInstance().getOut().isRunning()) {
            // fatal error
            StringWriter sw = new StringWriter();
            t.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            // show throwable and stack trace
            JOptionPane.showMessageDialog(null, new JScrollPane(new JTextArea() {{
                setFocusable(false);
                setText(exceptionAsString);
            }}), "Fatal Error", JOptionPane.ERROR_MESSAGE);
            System.exit(5);
        }
    }

    /**
     * @return all throwables that have been added to the NC
     */
    public @NotNull List<@NotNull Throwable> getThrowables() {
        return this.throwableList;
    }
}
