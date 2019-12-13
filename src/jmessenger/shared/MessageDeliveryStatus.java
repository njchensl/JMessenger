package jmessenger.shared;

import org.jetbrains.annotations.NotNull;

/**
 * After the client sends out a message, it waits until a status is received.
 *
 * @author frche1699
 */
public class MessageDeliveryStatus extends ServerMessage {
    private String message;
    private boolean successful;

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
        return "MESSAGE DELIVERY STATUS\n" + super.toString() + "\nServer message: " + message;
    }
}
