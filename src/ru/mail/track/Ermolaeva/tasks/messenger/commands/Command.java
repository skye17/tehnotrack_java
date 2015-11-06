package ru.mail.track.Ermolaeva.tasks.messenger.commands;

import ru.mail.track.Ermolaeva.tasks.messenger.net.CommandMessage;

public interface Command {
    Result execute(Object state, CommandMessage message);

    CommandType getName();

    String getDescription();

}
