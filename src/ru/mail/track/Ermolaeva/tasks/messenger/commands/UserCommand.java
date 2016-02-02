package ru.mail.track.Ermolaeva.tasks.messenger.commands;

import ru.mail.track.Ermolaeva.tasks.messenger.commands.command_message.UserMessage;
import ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.AbstractUserDao;
import ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.exceptions.DataAccessException;
import ru.mail.track.Ermolaeva.tasks.messenger.session.Session;

public class UserCommand extends MessengerCommand<UserMessage> {
    static final String NICKNAME_CHANGED = "Nickname is successfully changed";
    private AbstractUserDao userStore;

    public UserCommand(AbstractUserDao userStore) {
        this.userStore = userStore;
        commandType = CommandType.USER;
        description = "/user <nickname> - add nickname";
    }

    @Override
    protected Result executeCommand(Session session, UserMessage commandMessage) throws DataAccessException {

        String nickname = commandMessage.getNickname();
        if (nickname != null) {
            session.getCurrentUser().setNickname(nickname);
            userStore.setUpdateIndexes(4);
            userStore.update(session.getCurrentUser());
            return new CommandResult(NICKNAME_CHANGED);

        } else {
            return new CommandResult("Wrong nickname ", true);
        }
    }

}
