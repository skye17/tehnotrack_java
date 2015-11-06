package ru.mail.track.Ermolaeva.tasks.messenger.commands;


import ru.mail.track.Ermolaeva.tasks.messenger.command_message.LogoutMessage;
import ru.mail.track.Ermolaeva.tasks.messenger.session.Session;
import ru.mail.track.Ermolaeva.tasks.messenger.session.SessionManager;

public class LogoutCommand extends MessengerCommand<LogoutMessage> {
    private SessionManager sessionManager;

    public LogoutCommand(SessionManager sessionManager) {
        commandType = CommandType.LOGOUT;
        this.sessionManager = sessionManager;
        description = "/logout - close the session and leave account";
    }

    @Override
    protected Result executeCommand(Session session, LogoutMessage commandMessage) {
        sessionManager.closeSession(session.getId());
        return new CommandResult("Session closed");
    }
}
