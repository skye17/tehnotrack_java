package ru.mail.track.Ermolaeva.tasks.messenger.commands.exceptions;

public class IllegalCommandException extends IllegalArgumentException {
    public IllegalCommandException(String message) {
        super(message);
    }
}
