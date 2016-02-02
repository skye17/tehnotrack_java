package ru.mail.track.Ermolaeva.tasks.messenger.commands;

import ru.mail.track.Ermolaeva.tasks.messenger.commands.command_message.CommandMessage;
import ru.mail.track.Ermolaeva.tasks.messenger.commands.command_message.UserInfoMessage;
import ru.mail.track.Ermolaeva.tasks.messenger.commands.exceptions.IllegalCommandException;
import ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.AbstractUserDao;
import ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.exceptions.DataAccessException;
import ru.mail.track.Ermolaeva.tasks.messenger.session.Session;
import ru.mail.track.Ermolaeva.tasks.messenger.user.User;

import java.util.function.Function;

public class UserInfoCommand extends MessengerCommand<UserInfoMessage> {
    private AbstractUserDao userStore;

    public UserInfoCommand(AbstractUserDao userStore) {
        commandType = CommandType.USER_INFO;
        description = "/user_info [id] - information about user id";
        this.userStore = userStore;
    }

    @Override
    protected Result executeCommand(Session session, UserInfoMessage commandMessage) throws DataAccessException {
        Long userId = commandMessage.getUserId();
        if (userId == null) {
            userId = session.getCurrentUserId();
        }
        User user = userStore.getById(userId);

        if (user != null) {
            return new CommandResult(user.getInfo());
        } else {
            return new CommandResult("User with id " + userId + " is not registered", true);
        }
    }

    @Override
    public Function<String, ? extends CommandMessage> getArgumentParser() {
        return argInput -> {
            UserInfoMessage commandMessage = new UserInfoMessage();
            if (argInput.equals("")) {
                return commandMessage;
            } else {
                try {
                    Long value = Long.valueOf(argInput);
                    commandMessage.setUserId(value);
                    return commandMessage;
                } catch (NumberFormatException ex) {
                    throw new IllegalCommandException("User id should be a number");
                }
            }
        };
    }
}
