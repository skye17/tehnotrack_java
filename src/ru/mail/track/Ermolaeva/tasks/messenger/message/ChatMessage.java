package ru.mail.track.Ermolaeva.tasks.messenger.message;

import java.util.Calendar;
import java.util.Date;

public class ChatMessage extends Message {
    private Long chatId;
    private String message;
    private Date timestamp;

    public ChatMessage(Long id, Long senderId, Long chatId, Date timestamp, String message) {
        this(message, senderId, chatId);
        setId(id);
        setTimestamp(timestamp);
    }

    public ChatMessage(String message) {
        this.message = message;
        timestamp = Calendar.getInstance().getTime();
    }


    public ChatMessage(String message, Long senderId, Long chatId) {
        this(message);
        this.senderId = senderId;
        this.chatId = chatId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    @Override
    public String toString() {
        return "Message{" +
                "sender=" + senderId +
                ", timestamp=" + timestamp +
                ", chatId=" + chatId +
                ", message=" + message +
                "}";

    }
}
