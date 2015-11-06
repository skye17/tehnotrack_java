package ru.mail.track.Ermolaeva.tasks.messenger.command_message;


import ru.mail.track.Ermolaeva.tasks.messenger.commands.CommandType;
import ru.mail.track.Ermolaeva.tasks.messenger.net.CommandMessage;

public class UserInfoMessage extends CommandMessage {
    private Long userId;

    public UserInfoMessage() {
        super(CommandType.USER_INFO);
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
