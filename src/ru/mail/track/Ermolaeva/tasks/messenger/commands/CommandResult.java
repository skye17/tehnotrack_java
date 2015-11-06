package ru.mail.track.Ermolaeva.tasks.messenger.commands;

import org.codehaus.jackson.map.ObjectMapper;
import ru.mail.track.Ermolaeva.tasks.messenger.net.ResponseMessage;

import java.io.IOException;


public class CommandResult implements Result {
    private ResponseMessage message;
    private boolean isError;

    public CommandResult(Object object) {
        this(object, false);
    }

    public CommandResult(Object object, boolean errorStatus) {
        message = new ResponseMessage();
        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = mapper.writeValueAsString(object);
            message.setResponse(jsonString);
            message.setResultClass(object.getClass().getName());
            message.setStatus(errorStatus);
            isError = errorStatus;
        } catch (IOException io) {
            message.setStatus(true);
        }
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
