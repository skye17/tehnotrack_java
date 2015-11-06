package ru.mail.track.Ermolaeva.tasks.messenger.command_message;


import ru.mail.track.Ermolaeva.tasks.messenger.commands.CommandType;
import ru.mail.track.Ermolaeva.tasks.messenger.net.CommandMessage;

public class ChatFindMessage extends CommandMessage {
    private String regex;
    private Long chatId;

    public ChatFindMessage() {
        super(CommandType.CHAT_FIND);
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }
}
