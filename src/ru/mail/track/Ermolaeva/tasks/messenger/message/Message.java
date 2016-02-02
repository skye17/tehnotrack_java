package ru.mail.track.Ermolaeva.tasks.messenger.message;

import ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.Identified;

public abstract class Message implements Identified {
    protected Long id;
    protected Long senderId;


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