package ru.mail.track.Ermolaeva.tasks.messenger.commands;

import ru.mail.track.Ermolaeva.tasks.messenger.authorization.AuthorizationService;
import ru.mail.track.Ermolaeva.tasks.messenger.session.Session;
import ru.mail.track.Ermolaeva.tasks.messenger.session.User;

public class LoginCommand extends MessengerCommand {
    private AuthorizationService authorizationService;

    public LoginCommand(Session session, AuthorizationService authorizationService) {
        super(session);
        name = "login";
        description = "/login <username> <password> - login\n/login - sign in";
        this.authorizationService = authorizationService;
    }

    @Override
    public void execute(String argsString) {
        String[] arguments = preprocessArgumentsString(argsString);
        if (arguments.length == 0) {
            User user = authorizationService.createUser();
            if (user != null) {
                session.setCurrentUser(user);
            }
        } else {
            if (arguments.length == 2) {
                User user = authorizationService.login(arguments[0], arguments[1]);
                if (user != null) {
                    session.setCurrentUser(user);
                }
            } else {
                illegalArgument();
            }
        }
    }

}
