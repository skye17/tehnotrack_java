package ru.mail.track.Ermolaeva.tasks.messenger.commands;


import ru.mail.track.Ermolaeva.tasks.messenger.net.JsonProtocol;
import ru.mail.track.Ermolaeva.tasks.messenger.net.ObjectProtocol;
import ru.mail.track.Ermolaeva.tasks.messenger.net.ResponseMessage;



public class CommandResult implements Result {
    private ResponseMessage message;
    private boolean isError;

    public CommandResult(Object object) {
        this(object, false);
    }

    public CommandResult(Object object, boolean errorStatus) {


        // FIXME: надо вынести в Protocol (JsonProtocol implements Protocol)
        ObjectProtocol protocol = new JsonProtocol();
        message = protocol.decode(object);
        message.setStatus(message.getStatus() || errorStatus);
        isError = message.getStatus();

        /*message = new ResponseMessage();
        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = mapper.writeValueAsString(object);
            message.setResponse(jsonString);
            message.setResultClass(object.getClass().getName());
            message.setStatus(errorStatus);
            isError = errorStatus;
        } catch (IOException io) {
            message.setStatus(true);
        }*/
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
