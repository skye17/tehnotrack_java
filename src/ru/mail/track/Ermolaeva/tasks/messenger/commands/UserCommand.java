package ru.mail.track.Ermolaeva.tasks.messenger.commands;

import ru.mail.track.Ermolaeva.tasks.messenger.session.Session;
import ru.mail.track.Ermolaeva.tasks.messenger.session.User;

public class UserCommand extends MessengerCommand {

    public UserCommand(Session session) {
        super(session);
        name = "user";
        description = " /user <nickname> - add nickname\n" +
                "/user <old_pass> <new_pass> - change password (only after login)";
    }

    @Override
    public void execute(String argsString) {
        String[] arguments = preprocessArgumentsString(argsString);
        if (arguments.length == 1) {
            session.getCurrentUser().changeNickname(arguments[0]);

        } else {
            if (arguments.length == 2) {
                User user = session.getCurrentUser();
                user.changePassword(arguments[0], arguments[1]);
            } else {
                illegalArgument();
            }
        }
    }
}
