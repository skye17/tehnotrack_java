package ru.mail.track.Ermolaeva.tasks.messenger.command_message;


import ru.mail.track.Ermolaeva.tasks.messenger.commands.CommandType;
import ru.mail.track.Ermolaeva.tasks.messenger.net.CommandMessage;

public class ChatHistoryMessage extends CommandMessage {
    private Long chatId;

    public ChatHistoryMessage() {
        super(CommandType.CHAT_HISTORY);
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }
}
