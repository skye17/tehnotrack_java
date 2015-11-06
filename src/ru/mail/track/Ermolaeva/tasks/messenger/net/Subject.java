package ru.mail.track.Ermolaeva.tasks.messenger.net;

import ru.mail.track.Ermolaeva.tasks.messenger.session.Session;

public interface Subject {
    void addListener(MessageListener messageListener);

    void notifyListeners(Session session, SocketMessage message);
}
