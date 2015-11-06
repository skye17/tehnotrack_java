package ru.mail.track.Ermolaeva.tasks.messenger.commands;

import ru.mail.track.Ermolaeva.tasks.messenger.command_message.UserInfoMessage;
import ru.mail.track.Ermolaeva.tasks.messenger.session.Session;
import ru.mail.track.Ermolaeva.tasks.messenger.session.Store;

public class UserInfoCommand extends MessengerCommand<UserInfoMessage> {
    private Store userStore;

    public UserInfoCommand(Store userStore) {
        commandType = CommandType.USER_INFO;
        description = "/user_info [id] - information about user id";
        this.userStore = userStore;
    }

    @Override
    protected Result executeCommand(Session session, UserInfoMessage commandMessage) {
        Long userId = commandMessage.getUserId();
        if (userId == null) {
            userId = session.getCurrentUserId();
        }
        if (userStore.getUserById(userId) != null) {
            return new CommandResult(userStore.getUserById(userId).getInfo());
        } else {
            return new CommandResult("User with id " + userId + " is not registered", true);
        }
    }

}
