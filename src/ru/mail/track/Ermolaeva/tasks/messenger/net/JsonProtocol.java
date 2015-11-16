package ru.mail.track.Ermolaeva.tasks.messenger.net;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;


public class JsonProtocol implements ObjectProtocol {

    @Override
    public ResponseMessage decode(Object object) {
        ResponseMessage message = new ResponseMessage();
        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = mapper.writeValueAsString(object);
            message.setResponse(jsonString);
            message.setResultClass(object.getClass().getName());
        } catch (IOException io) {
            message.setStatus(true);
        }
        return message;
    }

    @Override
    public Object encode(ResponseMessage message) {
        ObjectMapper mapper = new ObjectMapper();
        Object object;
        Class objectClass;
        try {
            objectClass = Class.forName(message.getResultClass());
            object = mapper.readValue(message.getResponse(), objectClass);
            return object;
        } catch (Exception e) {
            return null;
        }
    }
}
