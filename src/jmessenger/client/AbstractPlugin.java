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

import jmessenger.shared.Message;
import jmessenger.shared.PluginMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

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

    public @Nullable JComponent getCustomJComponent() {
        return null;
    }

    public @Nullable PluginButton getCustomJButton() {
        return null;
    }

    public @Nullable JLabel renderCustomMessage(PluginMessage pm) {
        return null;
    }
}