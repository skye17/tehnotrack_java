package ru.mail.track.Ermolaeva.tasks.messenger.commands;

import ru.mail.track.Ermolaeva.tasks.messenger.authorization.AuthorizationService;
import ru.mail.track.Ermolaeva.tasks.messenger.exceptions.ExitException;
import ru.mail.track.Ermolaeva.tasks.messenger.session.Session;

import java.io.IOException;


public class ExitCommand extends MessengerCommand {
    private AuthorizationService authorizationService;

    public ExitCommand(AuthorizationService authorizationService, Session session) {
        super(session);
        name = "exit";
        description = "/exit - save changes and close the messenger";
        this.authorizationService = authorizationService;

    }

    @Override
    public void execute(String argsString) {
        try {
            session.close();
            if (authorizationService != null) {
                authorizationService.saveAndClose();
                throw new ExitException(0);
            } else {
                throw new ExitException(1);
            }
        } catch (IOException io) {
            throw new ExitException(io.getMessage(), 1);
        }

    }
}
