package ru.mail.track.Ermolaeva.tasks.messenger;
/*

import ru.mail.track.Ermolaeva.tasks.messenger.authorization.AuthorizationService;
import ru.mail.track.Ermolaeva.tasks.messenger.authorization.FileAuthorizationService;
import ru.mail.track.Ermolaeva.tasks.messenger.authorization.UserStore;
import ru.mail.track.Ermolaeva.tasks.messenger.commands.*;
import ru.mail.track.Ermolaeva.tasks.messenger.exceptions.ExitException;
import ru.mail.track.Ermolaeva.tasks.messenger.message.MessageService;
import ru.mail.track.Ermolaeva.tasks.messenger.session.DemoUser;
import ru.mail.track.Ermolaeva.tasks.messenger.session.Session;

import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws IOException {
        Session session = new Session();

        session.setCurrentUser(new DemoUser());

        UserStore userStore = new UserStore();
        AuthorizationService fileAuthorizationService = new FileAuthorizationService(userStore, "records.txt", false);
        session.setMessageService(new MessageService("history.txt", userStore));

        ArrayList<Command> commandsList = new ArrayList<>();
        commandsList.add(new LoginCommand(session, fileAuthorizationService));
        commandsList.add(new FindCommand(session));
        commandsList.add(new HistoryCommand(session));
        commandsList.add(new UserCommand(session));
        commandsList.add(new ExitCommand(fileAuthorizationService, session));
        HelpCommand helpCommand = new HelpCommand(commandsList);
        commandsList.add(helpCommand);

        try {
            new Interpreter(session, commandsList).run();
        } catch (ExitException ee) {
            System.exit(ee.getStatus());
        }

    }
}
*/