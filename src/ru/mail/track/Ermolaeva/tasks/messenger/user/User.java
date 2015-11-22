package ru.mail.track.Ermolaeva.tasks.messenger.user;

import ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.Identified;

public class User implements Identified {
    private Long id;
    private String name;
    private String password;
    private String nickname;


    public User() {
    }



    public User(String username, String userPassword, String userNickname) {
        this(username, userPassword);
        nickname = userNickname;
    }

    public User(String username, String userPassword) {
        name = username;
        password = userPassword;
    }

    public User(Long id, String username, String userPassword, String userNickname) {
        this(username, userPassword, userNickname);
        setId(id);
    }


    public User(Long id, String username, String userPassword) {
        this(username, userPassword);
        setId(id);
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInfo() {
        return String.format("Id: %d\nUsername: %s\nNickname: %s\n", id, name, nickname);
    }

}
