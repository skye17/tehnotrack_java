package ru.mail.track.Ermolaeva.tasks.messenger.message;

import ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.Identified;

import java.util.ArrayList;
import java.util.List;

public class Chat implements Identified {
    private Long id;
    private String title;

    private List<Long> messageList;
    private List<Long> usersList;

    public Chat() {
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

    public String getInfo() {
        return String.format("Id: %d\nParticipants' ids: %s\nMessages' ids: %s\nTitle: %s\n",
                id, usersList, messageList, title);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
