package ru.mail.track.Ermolaeva.tasks.messenger.commands;

import ru.mail.track.Ermolaeva.tasks.messenger.command_message.UserMessage;
import ru.mail.track.Ermolaeva.tasks.messenger.session.Session;

public class UserCommand extends MessengerCommand<UserMessage> {
    static final String NICKNAME_CHANGED = "Nickname is successfully changed";

    public UserCommand() {
        commandType = CommandType.USER;
        description = "/user <nickname> - add nickname";
    }

    @Override
    protected Result executeCommand(Session session, UserMessage commandMessage) {
        String nickname = commandMessage.getNickname();
        if (nickname != null) {
            session.getCurrentUser().setNickname(nickname);
            return new CommandResult(NICKNAME_CHANGED);
        } else {
            return new CommandResult("Wrong nickname ", true);
        }
    }
}
