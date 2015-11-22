package ru.mail.track.Ermolaeva.tasks.messenger.commands;


import ru.mail.track.Ermolaeva.tasks.messenger.net.ObjectProtocol;
import ru.mail.track.Ermolaeva.tasks.messenger.net.ResponseMessage;


public class CommandResult implements Result {
    private static ObjectProtocol protocol;
    private ResponseMessage message;
    private boolean isError;

    public CommandResult(Object object) {
        this(object, false);
    }

    public CommandResult(Object object, boolean errorStatus) {
        message = protocol.decode(object);
        message.setStatus(message.getStatus() || errorStatus);
        isError = message.getStatus();
    }

    public static void setProtocol(ObjectProtocol protocol) {
        CommandResult.protocol = protocol;
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
