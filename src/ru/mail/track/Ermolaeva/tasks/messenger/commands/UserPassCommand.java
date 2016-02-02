package ru.mail.track.Ermolaeva.tasks.messenger.commands;


import ru.mail.track.Ermolaeva.tasks.messenger.commands.command_message.UserPassMessage;
import ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.AbstractUserDao;
import ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.exceptions.DataAccessException;
import ru.mail.track.Ermolaeva.tasks.messenger.session.Session;
import ru.mail.track.Ermolaeva.tasks.messenger.user.User;

public class UserPassCommand extends MessengerCommand<UserPassMessage> {
    static final String PASSWORD_CHANGED = "Password is successfully changed";
    static final String WRONG_INFO = "Wrong information. Password is not changed";

    private AbstractUserDao userStore;

    public UserPassCommand(AbstractUserDao userStore) {
        commandType = CommandType.USER_PASS;
        description = "/user_pass <old_pass> <new_pass> - change password";
        this.userStore = userStore;
    }

    @Override
    protected Result executeCommand(Session session, UserPassMessage commandMessage) throws DataAccessException {
        User user = session.getCurrentUser();
        String oldPassword = commandMessage.getOldPassword();
        String newPassword = commandMessage.getNewPassword();
        if (userStore.getUser(user.getName(), oldPassword) != null && newPassword != null) {
            user.setPassword(newPassword);
            userStore.setUpdateIndexes(3);
            userStore.update(user);
            return new CommandResult(PASSWORD_CHANGED);
        } else {
            return new CommandResult(WRONG_INFO, true);
        }
    }
}
