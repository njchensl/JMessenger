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

package jmessenger.shared;

import org.jetbrains.annotations.NotNull;

/**
 * After the client sends out a message, it waits until a status is received.
 *
 * @author frche1699
 */
public class MessageDeliveryStatus extends ServerMessage {
    protected String message;
    protected boolean successful;

    public MessageDeliveryStatus() {
        super();
        this.message = "";
        this.successful = true;
    }

    /**
     * @param msg what the server says
     */
    public MessageDeliveryStatus(@NotNull String msg) {
        this();
        this.message = msg;
    }

    /**
     * @param status the delivery status
     */
    public MessageDeliveryStatus(boolean status) {
        this();
        this.successful = status;
    }

    /**
     * @param status the delivery status
     * @param msg the message
     */
    public MessageDeliveryStatus(boolean status, @NotNull String msg) {
        this();
        this.successful = status;
        this.message = msg;
    }

    /**
     * @return what the server says
     */
    @NotNull
    public String getMessage() {
        return this.message;
    }

    /**
     * @param msg what the server says
     */
    public void setMessage(@NotNull String msg) {
        this.message = msg;
    }

    /**
     * @return the delivery status
     */
    public boolean isSuccessful() {
        return successful;
    }

    /**
     * @param successful the delivery status
     */
    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    @NotNull
    @Override
    public String toString() {
        return "MESSAGE DELIVERY STATUS\n" + super.toString() + "\nServer message: " + message + "\nStatus: " + successful;
    }
}
