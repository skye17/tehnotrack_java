package ru.mail.track.Ermolaeva.tasks.messenger.commands.command_message;


import ru.mail.track.Ermolaeva.tasks.messenger.commands.CommandType;

import java.util.List;

public class ChatCreateMessage extends ChatCommandMessage {
    private List<Long> userIdList;

    public ChatCreateMessage() {
        super(CommandType.CHAT_CREATE);
    }

    public List<Long> getUserIdList() {
        return userIdList;
    }

    public void setUserIdList(List<Long> userIdList) {
        this.userIdList = userIdList;
    }

}
