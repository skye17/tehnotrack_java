package ru.mail.track.Ermolaeva.tasks.messenger.commands;

import ru.mail.track.Ermolaeva.tasks.messenger.net.CommandMessage;
import ru.mail.track.Ermolaeva.tasks.messenger.session.Session;


public abstract class MessengerCommand<T extends CommandMessage> implements Command {
    protected CommandType commandType;
    protected String description;
    protected boolean needLogin = true;

    public MessengerCommand() {
    }

    @Override
    public Result execute(Object state, CommandMessage message) {
        if (state != null && state instanceof Session) {
            Session session = (Session) state;
            if (needLogin && !checkIsLogin(session)) {
                return new CommandResult("You need to login first", true);
            }
            return executeCommand(session, (T) message);
        }
        return new CommandResult("Illegal command message", true);
    }


    protected abstract Result executeCommand(Session session, T commandMessage);


    @Override
    public CommandType getName() {
        return commandType;
    }

    public void setCommandType(CommandType commandType) {
        this.commandType = commandType;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    protected boolean checkIsLogin(Session session) {
        return session.getCurrentUser() != null;
    }

}
