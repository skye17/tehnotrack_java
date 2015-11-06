package ru.mail.track.Ermolaeva.tasks.messenger.message.command_message;

import ru.mail.track.Ermolaeva.tasks.messenger.commands.CommandType;
import ru.mail.track.Ermolaeva.tasks.messenger.net.CommandMessage;

import java.util.List;

public class ChatCommandMessage extends CommandMessage {
    private String regex;
    private List<Long> userIdList;
    private Long chatId;
    private String messageToChat;

    public ChatCommandMessage() {
        super(CommandType.CHAT);
    }

    public List<Long> getUserIdList() {
        return null;
    }

    public void setUserIdList(List<Long> userIdList) {
        this.userIdList = userIdList;
    }

    public Long getChatId() {
        return null;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getRegex() {
        return null;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public String getMessageToChat() {
        return null;
    }

    public void setMessageToChat(String messageToChat) {
        this.messageToChat = messageToChat;
    }
}

