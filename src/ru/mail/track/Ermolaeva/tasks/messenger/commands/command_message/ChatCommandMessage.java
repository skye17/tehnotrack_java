package ru.mail.track.Ermolaeva.tasks.messenger.commands.command_message;

import ru.mail.track.Ermolaeva.tasks.messenger.commands.CommandType;


public class ChatCommandMessage extends CommandMessage {
    private Long chatId;

    public ChatCommandMessage(CommandType commandType) {
        super(commandType);
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }
}
