package ru.mail.track.Ermolaeva.tasks.messenger.session;


import ru.mail.track.Ermolaeva.tasks.messenger.net.ConnectionHandler;


public class Session {
    private Long id;
    private User currentUser;
    private ConnectionHandler connectionHandler;

    public Session(Long id) {
        this.id = id;
    }

    public Session() {
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        currentUser = user;
    }


    public ConnectionHandler getConnectionHandler() {
        return connectionHandler;
    }

    public void setConnectionHandler(ConnectionHandler connectionHandler) {
        this.connectionHandler = connectionHandler;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCurrentUserId() {
        return currentUser.getId();
    }
}
