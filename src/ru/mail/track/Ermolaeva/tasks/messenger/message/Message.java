package ru.mail.track.Ermolaeva.tasks.messenger.message;

import java.util.Date;

public class Message {
    private String message;
    private String timestamp;

    public Message(String message, String timestamp) {
        this.message = message;
        setTimestamp(timestamp);
    }

    public Message(String message, Date timestamp) {
        this.message = message;
        setTimestamp(timestamp);
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp.toString();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }


}
