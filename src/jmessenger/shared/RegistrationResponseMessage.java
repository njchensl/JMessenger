package jmessenger.shared;

import org.jetbrains.annotations.NotNull;

public class RegistrationResponseMessage extends ServerMessage {
    private int userID;

    /**
     * @param userID the assigned user id
     */
    public RegistrationResponseMessage(int userID) {
        this.userID = userID;
    }

    /**
     * @return the assigned user id
     */
    public int getUserID() {
        return userID;
    }

    /**
     * @param userID the assigned user id
     */
    public void setUserID(int userID) {
        this.userID = userID;
    }

    @NotNull
    @Override
    public String toString() {
        return "REGISTRATION RESPONSE MESSAGE\n" + super.toString() + "\nAssigned user ID: " + userID;
    }
}
