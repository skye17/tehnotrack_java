package ru.mail.track.Ermolaeva.tasks.messenger.commands.command_message;


import ru.mail.track.Ermolaeva.tasks.messenger.commands.CommandType;

public class ChatFindMessage extends ChatCommandMessage {
    private String regex;

    public ChatFindMessage() {
        super(CommandType.CHAT_FIND);
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

}
