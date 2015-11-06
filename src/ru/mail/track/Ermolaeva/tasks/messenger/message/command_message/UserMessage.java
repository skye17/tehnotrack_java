package ru.mail.track.Ermolaeva.tasks.messenger.message.command_message;

import ru.mail.track.Ermolaeva.tasks.messenger.commands.CommandType;
import ru.mail.track.Ermolaeva.tasks.messenger.net.CommandMessage;

public class UserMessage extends CommandMessage {
    private String nickname;
    private Long userId;
    private String oldPassword;
    private String newPassword;

    public UserMessage() {
        super(CommandType.USER);
    }

    public String getNickname() {
        return null;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Long getUserId() {
        return null;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getOldPassword() {
        return null;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return null;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

}
