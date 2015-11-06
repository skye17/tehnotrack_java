package ru.mail.track.Ermolaeva.tasks.messenger.session;

public class User {
    private static long idCounter;
    private Long id;
    private String name;
    private String password;
    private String nickname;

    public User() {
    }

    public User(String username, String userPassword, String nickname) {
        this(username, userPassword);
        this.nickname = nickname;
    }

    public User(String username, String userPassword) {
        name = username;
        password = userPassword;
        id = idCounter++;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getInfo() {
        return String.format("Id: %d\nUsername: %s\nNickname: %s\n", id, name, nickname);
    }

}
