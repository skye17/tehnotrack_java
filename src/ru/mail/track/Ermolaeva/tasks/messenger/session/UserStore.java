package ru.mail.track.Ermolaeva.tasks.messenger.session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserStore implements Store {
    protected Map<Long, User> accounts;

    public UserStore() {
        accounts = new HashMap<>();
    }

    @Override
    public User addUser(User user) {
        if (user != null) {
            accounts.put(user.getId(), user);
            return user;
        }
        return null;
    }

    @Override
    public User getUser(String username, String password) {
        if (username != null && password != null) {
            for (User user : accounts.values()) {
                if (user.getName().equals(username) && user.getPassword().equals(password)) {
                    return user;
                }
            }
        }
        return null;
    }

    @Override
    public int size() {
        return accounts.size();
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(accounts.values());
    }

    @Override
    public User getUserById(Long id) {
        if (id != null) {
            return accounts.get(id);
        }
        return null;
    }

    @Override
    public boolean isUserExist(String username) {
        for (User user : accounts.values()) {
            if (user.getName().equals(username)) {
                return true;
            }
        }
        return false;
    }
}
