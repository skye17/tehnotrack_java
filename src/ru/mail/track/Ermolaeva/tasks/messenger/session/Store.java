package ru.mail.track.Ermolaeva.tasks.messenger.session;


import java.util.Collection;

public interface Store {
    User addUser(User user);

    User getUser(String name, String password);

    int size();

    Collection<User> getUsers();

    User getUserById(Long id);

    boolean isUserExist(String username);

}
