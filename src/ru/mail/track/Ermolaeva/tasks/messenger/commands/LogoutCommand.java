package ru.mail.track.Ermolaeva.tasks.messenger.commands;


import ru.mail.track.Ermolaeva.tasks.messenger.commands.command_message.CommandMessage;
import ru.mail.track.Ermolaeva.tasks.messenger.session.Session;
import ru.mail.track.Ermolaeva.tasks.messenger.session.SessionManager;


public class LogoutCommand extends MessengerCommand<CommandMessage> {
    private SessionManager sessionManager;

    public LogoutCommand(SessionManager sessionManager) {
        commandType = CommandType.LOGOUT;
        this.sessionManager = sessionManager;
        description = "/logout - close the session and leave account";
    }

    @Override
    protected Result executeCommand(Session session, CommandMessage commandMessage) {
        sessionManager.closeSession(session.getId());
        return new CommandResult("Session closed");
    }
}
