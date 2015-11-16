package ru.mail.track.Ermolaeva.tasks.messenger.net;

public interface ObjectProtocol {
    ResponseMessage decode(Object object);

    Object encode(ResponseMessage msg);
}
