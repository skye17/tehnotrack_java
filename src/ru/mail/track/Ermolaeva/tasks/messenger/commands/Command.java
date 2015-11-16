package ru.mail.track.Ermolaeva.tasks.messenger.commands;

import ru.mail.track.Ermolaeva.tasks.messenger.commands.command_message.CommandMessage;
import ru.mail.track.Ermolaeva.tasks.messenger.session.Session;

import java.util.function.Function;

public interface Command {
    Result execute(Session state, CommandMessage message);

    CommandType getType();

    String getDescription();

    Function<String, ? extends CommandMessage> getArgumentParser();

    void setArgumentParser(Function<String, ? extends CommandMessage> argumentParser);

}
