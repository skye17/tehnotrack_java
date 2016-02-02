package ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.exceptions;

public class IllegalDataStateException extends IllegalStateException {
    public IllegalDataStateException(String message) {
        super(message);
    }

    public IllegalDataStateException(Exception ex) {
        super(ex);
    }
}
