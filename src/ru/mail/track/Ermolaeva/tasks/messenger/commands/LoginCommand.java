package ru.mail.track.Ermolaeva.tasks.messenger.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mail.track.Ermolaeva.tasks.messenger.command_message.LoginMessage;
import ru.mail.track.Ermolaeva.tasks.messenger.session.Session;
import ru.mail.track.Ermolaeva.tasks.messenger.session.SessionManager;
import ru.mail.track.Ermolaeva.tasks.messenger.session.Store;
import ru.mail.track.Ermolaeva.tasks.messenger.session.User;


public class LoginCommand extends MessengerCommand<LoginMessage> {
    static Logger log = LoggerFactory.getLogger(LoginCommand.class);
    private Store userStore;
    private SessionManager sessionManager;

    public LoginCommand(Store userStore, SessionManager sessionManager) {
        commandType = CommandType.LOGIN;
        description = "/login <username> <password> - login/sign in";
        needLogin = false;
        this.userStore = userStore;
        this.sessionManager = sessionManager;
    }

    @Override
    protected Result executeCommand(Session session, LoginMessage commandMessage) {
        if (session.getCurrentUser() != null) {
            log.info("User {} already logged in.", session.getCurrentUser());
            String errorMessage = String.format("User %d already logged in", session.getCurrentUserId());
            return new CommandResult(errorMessage, true);
        } else {
            String login = commandMessage.getLogin();
            String password = commandMessage.getPassword();
            User user = userStore.getUser(login, password);
            if (user == null) {
                if (userStore.isUserExist(login)) {
                    return new CommandResult("Wrong password", true);
                } else {
                    User newUser = new User(login, password);
                    userStore.addUser(newUser);
                    return new CommandResult("You successfully signed up. To continue, you need to log in.");
                }
            }
            if (sessionManager.getSessionByUser(user.getId()) != null) {
                return new CommandResult("Somebody already logged in your account.", true);
            }
            session.setCurrentUser(user);
            sessionManager.registerUser(user.getId(), session.getId());
            log.info("Success login: {}", user);
            return new CommandResult(user.getInfo());
        }
    }

}
