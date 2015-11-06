package ru.mail.track.Ermolaeva.tasks.messenger.message;

import java.util.ArrayList;
import java.util.List;

public class Chat {
    private static long idCounter;
    private Long id;

    private List<Long> messageList;
    private List<Long> usersList;

    public Chat() {
        id = idCounter++;
        messageList = new ArrayList<>();
        usersList = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Long> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<Long> messageList) {
        this.messageList = messageList;
    }

    public List<Long> getUsersList() {
        return usersList;
    }

    public void setUsersList(List<Long> usersList) {
        this.usersList = usersList;
    }

    public void addUser(Long userId) {
        if (userId != null) {
            usersList.add(userId);
        }
    }

    public void removeUser(Long userId) {
        if (userId != null) {
            usersList.remove(userId);
        }

    }

    public void addMessage(Long messageId) {
        if (messageId != null && !messageList.contains(messageId)) {
            messageList.add(messageId);
        }
    }

    public String getInfo() {
        return String.format("Id: %d\nParticipants' ids: %s\nMessages' ids: %s\n", id, usersList, messageList);
    }

}
