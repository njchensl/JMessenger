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

import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Gary Gao
 */
public abstract class Message implements java.io.Serializable {

    protected final String messageID;

    /**
     * constructs a message object with a 16 character long (including letters
     * and numbers) message ID
     */
    public Message() {
        messageID = RandomStringUtils.random(16, true, true);
    }

    /**
     * @return the message ID
     */
    @NotNull
    public String getMessageID() {
        return this.messageID;
    }

    @NotNull
    @Override
    public String toString() {
        return "Message ID: " + messageID;
    }

}
