package ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.exceptions;


public class DataAccessException extends Exception {
    public DataAccessException(String message) {
        super(message);
    }

    public DataAccessException(Exception ex) {
        super(ex);
    }
}
