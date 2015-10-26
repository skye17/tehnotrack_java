package ru.mail.track.Ermolaeva.tasks.messenger.commands;

import ru.mail.track.Ermolaeva.tasks.messenger.session.Session;
import ru.mail.track.Ermolaeva.tasks.messenger.session.User;

public class UserCommand extends MessengerCommand {
    static final String DEMO_USER = "DemoUser";
    static final String NICKNAME_CHANGED = "Nickname is successfully changed";
    static final String PASSWORD_CHANGED = "Password is successfully changed";
    static final String WRONG_INFO = "Wrong information. Password is not changed";


    public UserCommand(Session session) {
        super(session);
        name = "user";
        description = " /user <nickname> - add nickname\n" +
                "/user <old_pass> <new_pass> - change password (only after login)";
    }

    @Override
    public CommandResult execute(String argsString) {
        String[] arguments = preprocessArgumentsString(argsString);

        if (arguments.length == 1) {
            String nickname = arguments[0];
            boolean realUser = session.getCurrentUser().setNickname(nickname);
            if (nickname != null) {
                if (realUser) {
                    return out -> out.println(NICKNAME_CHANGED);
                }
            }
        } else {
            if (arguments.length == 2) {
                User user = session.getCurrentUser();
                String oldPassword = arguments[0];
                String newPassword = arguments[1];
                if (oldPassword.equals(user.getPassword()) && newPassword != null) {
                    boolean realUser = user.setPassword(newPassword);
                    if (realUser) {
                        return out -> out.println(PASSWORD_CHANGED);
                    }
                } else {
                    if (!user.getName().equals(DEMO_USER)) {
                        return out -> out.println(WRONG_INFO);
                    }
                }
            } else {
                illegalArgument();
            }
        }
        return null;
    }
}
