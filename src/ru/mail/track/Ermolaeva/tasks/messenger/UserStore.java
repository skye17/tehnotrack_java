package ru.mail.track.Ermolaeva.tasks.messenger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserStore implements Store {
    protected Map<String, User> accounts;

    public UserStore() {
        accounts = new HashMap<>();
    }

    @Override
    public boolean isUserExist(String name) {
        Utility.isNull(name);
        return accounts.containsKey(name);
    }

    @Override
    public void addUser(User user) {
        Utility.isNull(user);
        accounts.put(user.getName(), user);
    }

    @Override
    public User getUser(String username, String password) {
        Utility.isNull(username, password);
        if (checkUserPassword(username, password)) {
            return accounts.get(username);
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

    private boolean checkUserPassword(String username, String password) {
        return accounts.get(username).getPassword().compareTo(password) == 0;
    }
}
