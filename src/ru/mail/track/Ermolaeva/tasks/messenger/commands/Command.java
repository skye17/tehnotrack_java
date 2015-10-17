package ru.mail.track.Ermolaeva.tasks.messenger.commands;

public interface Command {

    String getName();

    void execute(String argString);

    String getDescription();

}
