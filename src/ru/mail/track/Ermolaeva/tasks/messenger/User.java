package ru.mail.track.Ermolaeva.tasks.messenger;

public class User {
    private String name;
    private String password;

    public User(String username, String userPassword) {
        name = username;
        password = userPassword;
    }

    public String getName() {
        return  name;
    }

    public String getPassword() {
        return password;
    }


}