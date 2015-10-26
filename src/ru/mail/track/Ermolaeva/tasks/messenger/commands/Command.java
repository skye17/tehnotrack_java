package ru.mail.track.Ermolaeva.tasks.messenger.commands;

public interface Command {

    String getName();

    CommandResult execute(String argString);

    String getDescription();

}
