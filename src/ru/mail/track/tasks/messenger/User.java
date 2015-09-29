package ru.mail.track.tasks.messenger;

public class User {
    private String name;
    private String password;

    public String getName() {
        return  name;
    }

    public String getPassword() {
        return password;
    }

    public User(String username, String userPassword) {
        name = username;
        password = userPassword;
    }

}
