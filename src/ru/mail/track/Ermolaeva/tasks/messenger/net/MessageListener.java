package ru.mail.track.Ermolaeva.tasks.messenger.net;

import ru.mail.track.Ermolaeva.tasks.messenger.session.Session;

public interface MessageListener {
    void update(Session session, SocketMessage message);
}
