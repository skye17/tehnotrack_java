package ru.mail.track.Ermolaeva.tasks.messenger.message;

public abstract class Message {
    protected Long id;
    protected Long senderId;

    public Message() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSender() {
        return senderId;
    }

    public void setSender(Long sender) {
        senderId = sender;
    }

}