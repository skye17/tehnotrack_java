package ru.mail.track.Ermolaeva.tasks.messenger.session;

import ru.mail.track.Ermolaeva.tasks.messenger.user.User;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class SessionManager {
    private volatile static SessionManager uniqueManager;
    private Map<Long, Session> sessionMap;
    private Map<Long, Long> userSession;
    private AtomicLong sessionCounter = new AtomicLong(0);

    private SessionManager() {
        sessionMap = new HashMap<>();
        userSession = new HashMap<>();
    }

    public static SessionManager getInstance() {
        if (uniqueManager == null) {
            synchronized (SessionManager.class) {
                if (uniqueManager == null) {
                    uniqueManager = new SessionManager();
                }
            }
        }
        return uniqueManager;
    }

    public Session createSession() {
        Long id = sessionCounter.getAndIncrement();
        Session session = new Session(id);
        sessionMap.put(id, session);
        return session;
    }

    public Session getSession(Long id) {
        return sessionMap.get(id);
    }

    public void registerUser(Long userId, Long sessionId) {
        userSession.put(userId, sessionId);
    }

    public Session getSessionByUser(Long userId) {
        return sessionMap.get(userSession.get(userId));
    }

    public void closeSession(Long id) {
        Session session = sessionMap.get(id);
        User sessionUser = session.getCurrentUser();
        if (userSession.get(sessionUser.getId()).equals(session.getId())) {
            userSession.remove(sessionUser.getId());
        }
        session.setCurrentUser(null);
    }
}
