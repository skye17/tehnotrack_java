package ru.mail.track.Ermolaeva.tasks.messenger.net;

import java.io.IOException;

public interface ConnectionHandler extends Runnable, Subject {

    void send(SocketMessage msg) throws IOException;

    void stop();
}