package ru.mail.track.Ermolaeva.tasks.messenger.net;

import ru.mail.track.Ermolaeva.tasks.messenger.message.MessageType;

public class ResponseMessage extends SocketMessage {
    private String response;
    private String resultClassName;

    private Object responseObject;
    private boolean status;

    public ResponseMessage() {
        setMessageType(MessageType.RESPONSE);
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getResultClass() {
        return resultClassName;
    }

    public void setResultClass(String resultClassName) {
        this.resultClassName = resultClassName;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Object getResponseObject() {
        return responseObject;
    }

    public void setResponseObject(Object responseObject) {
        this.responseObject = responseObject;
    }
}
