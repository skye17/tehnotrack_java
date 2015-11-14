package ru.mail.track.Ermolaeva.tasks.messenger.commands;

import ru.mail.track.Ermolaeva.tasks.messenger.commands.command_message.CommandMessage;

import java.util.function.Function;

public interface Command {
    Result execute(Object state, CommandMessage message);

    CommandType getName();

    String getDescription();

    Function<String, ? extends CommandMessage> getArgumentParser();

    void setArgumentParser(Function<String, ? extends CommandMessage> argumentParser);

}
