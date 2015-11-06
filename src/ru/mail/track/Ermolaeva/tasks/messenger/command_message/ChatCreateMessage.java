package ru.mail.track.Ermolaeva.tasks.messenger.command_message;


import ru.mail.track.Ermolaeva.tasks.messenger.commands.CommandType;
import ru.mail.track.Ermolaeva.tasks.messenger.net.CommandMessage;

import java.util.List;

public class ChatCreateMessage extends CommandMessage {
    private List<Long> userIdList;
    private Long chatId;

    public ChatCreateMessage() {
        super(CommandType.CHAT_CREATE);
    }

    public List<Long> getUserIdList() {
        return userIdList;
    }

    public void setUserIdList(List<Long> userIdList) {
        this.userIdList = userIdList;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }
}
