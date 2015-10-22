package ru.mail.track.Ermolaeva.tasks.messenger.session;

public class User {
    private String name;
    private String password;
    private String nickname;

    public User(String username, String userPassword, String nickname) {
        this(username, userPassword);
        this.nickname = nickname;
    }

    public User(String username, String userPassword) {
        name = username;
        password = userPassword;
    }

    public String getNickname() {
        return nickname;
    }

    public String getName() {
        return  name;
    }

    public String getPassword() {
        return password;
    }

    public boolean setPassword(String password) {
        this.password = password;
        return true;
    }


    public boolean setNickname(String nickname) {
        this.nickname = nickname;
        return true;
    }

}
