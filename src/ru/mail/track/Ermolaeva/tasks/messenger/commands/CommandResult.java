package ru.mail.track.Ermolaeva.tasks.messenger.commands;


import java.io.IOException;
import java.io.PrintStream;

public interface CommandResult {
    void show(PrintStream out) throws IOException;
}
