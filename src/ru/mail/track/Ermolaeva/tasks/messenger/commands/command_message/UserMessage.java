package ru.mail.track.Ermolaeva.tasks.messenger.commands.command_message;

import ru.mail.track.Ermolaeva.tasks.messenger.commands.CommandType;

public class UserMessage extends CommandMessage {
    private String nickname;

    public UserMessage() {
        super(CommandType.USER);
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

}
