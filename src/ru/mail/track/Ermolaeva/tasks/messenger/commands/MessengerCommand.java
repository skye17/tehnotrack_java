package ru.mail.track.Ermolaeva.tasks.messenger.commands;

import ru.mail.track.Ermolaeva.tasks.messenger.commands.command_message.CommandMessage;
import ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.exceptions.DataAccessException;
import ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.exceptions.IllegalDataStateException;
import ru.mail.track.Ermolaeva.tasks.messenger.session.Session;

import java.util.function.Function;


public abstract class MessengerCommand<T extends CommandMessage> implements Command {
    protected CommandType commandType;
    protected String description;
    protected boolean needLogin = true;
    protected Function<String, ? extends CommandMessage> argumentParser;

    public MessengerCommand() {
    }

    public MessengerCommand(Function<String, ? extends CommandMessage> argumentParser) {
        this.argumentParser = argumentParser;
    }

    @Override
    public Function<String, ? extends CommandMessage> getArgumentParser() {
        return argumentParser;
    }

    @Override
    public void setArgumentParser(Function<String, ? extends CommandMessage> argumentParser) {
        this.argumentParser = argumentParser;
    }

    @Override
    public Result execute(Session state, CommandMessage message) {
        if (state != null) {
            if (needLogin && !checkIsLogin(state)) {
                return new CommandResult("You need to login first", true);
            }
            try {
                return executeCommand(state, (T) message);
            } catch (DataAccessException | IllegalDataStateException e) {
                return new CommandResult(e.getMessage(), true);
            }
        }
        return new CommandResult("Illegal command message", true);
    }


    protected abstract Result executeCommand(Session state, T commandMessage) throws DataAccessException;


    @Override
    public CommandType getType() {
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
