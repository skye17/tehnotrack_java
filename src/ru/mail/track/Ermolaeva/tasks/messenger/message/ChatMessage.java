package ru.mail.track.Ermolaeva.tasks.messenger.message;

import java.util.Calendar;
import java.util.Date;

public class ChatMessage extends Message {
    private static long idCounter;

    private Long chatId;
    private String message;
    private String timestamp;

    public ChatMessage() {
    }

    public ChatMessage(String message) {
        id = idCounter++;
        this.message = message;
        timestamp = Calendar.getInstance().getTime().toString();
    }


    public ChatMessage(String message, long senderId) {
        this(message);
        this.senderId = senderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp.toString();
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
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
                ", sender=" + senderId +
                ", timestamp=" + timestamp +
                ", chatId=" + chatId +
                ", message=" + message +
                '}';
    }
}
