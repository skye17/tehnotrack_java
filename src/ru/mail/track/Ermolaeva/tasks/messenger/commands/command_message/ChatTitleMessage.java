package ru.mail.track.Ermolaeva.tasks.messenger.commands.command_message;

import ru.mail.track.Ermolaeva.tasks.messenger.commands.CommandType;

public class ChatTitleMessage extends ChatCommandMessage {
    private String chatTitle;

    public ChatTitleMessage() {
        super(CommandType.CHAT_TITLE);
    }

    public String getChatTitle() {
        return chatTitle;
    }

    public void setChatTitle(String chatTitle) {
        this.chatTitle = chatTitle;
    }
}
