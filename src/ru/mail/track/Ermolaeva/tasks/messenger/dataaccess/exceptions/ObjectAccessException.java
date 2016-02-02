package ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.exceptions;

public class ObjectAccessException extends Exception {
    public ObjectAccessException(String message) {
        super(message);
    }

    public ObjectAccessException(Exception ex) {
        super(ex);
    }

    public ObjectAccessException(String message, Exception ex) {
        super(message, ex);
    }
}
