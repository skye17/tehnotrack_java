package ru.mail.track.Ermolaeva.tasks.messenger.exceptions;

/*
* The exceptions is used for safe program exit.
* Its status determines exit code.
 */
public class ExitException extends RuntimeException {
    private final int status;

    public ExitException(final String message, final int exitStatus) {
        super(message);
        status = exitStatus;
    }

    public ExitException(final int exitStatus) {
        super("");
        status = exitStatus;
    }

    public final int getStatus() {
        return status;
    }
}
