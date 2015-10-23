package ru.mail.track.Ermolaeva.tasks.messenger.commands;

import ru.mail.track.Ermolaeva.tasks.messenger.session.Session;
import ru.mail.track.Ermolaeva.tasks.messenger.session.User;

public class UserCommand extends MessengerCommand {
    static final String DEMO_USER = "DemoUser";

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
            String nickname = arguments[0];
            boolean realUser = session.getCurrentUser().setNickname(nickname);
            if (nickname != null) {
                if (realUser) {
                    System.out.println("Nickname is successfully changed");
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
                        System.out.println("Password is successfully changed");
                    }
                } else {
                    if (!user.getName().equals(DEMO_USER)) {
                        System.out.println("Wrong information. Password is not changed");
                    }
                }
            } else {
                illegalArgument();
            }
        }
    }
}
