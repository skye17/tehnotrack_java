package ru.mail.track.Ermolaeva.tasks.messenger.net;

public interface Protocol {
    SocketMessage decode(byte[] bytes);

    byte[] encode(SocketMessage msg);
}
