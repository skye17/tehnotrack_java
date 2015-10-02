package ru.mail.track.Ermolaeva.tasks.messenger;


import java.util.Collection;

public interface Store {
    boolean isUserExist(String name);

    void addUser(User user);

    User getUser(String name, String password);

    int size();

    Collection<User> getUsers();

}
