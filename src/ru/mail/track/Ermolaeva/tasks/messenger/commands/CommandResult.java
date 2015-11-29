package ru.mail.track.Ermolaeva.tasks.messenger.commands;


import ru.mail.track.Ermolaeva.tasks.messenger.net.ResponseMessage;


public class CommandResult implements Result {
    private ResponseMessage message;
    private boolean isError;

    public CommandResult(Object object) {
        this(object, false);
    }

    public CommandResult(Object object, boolean errorStatus) {
        message = new ResponseMessage();
        message.setResponseObject(object);
        message.setStatus(errorStatus);
        isError = errorStatus;
    }

    @Override
    public ResponseMessage getMessage() {
        return message;
    }

    @Override
    public boolean getStatus() {
        return isError;
    }

}
