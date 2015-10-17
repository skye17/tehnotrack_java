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

    public void changeNickname(String nickname) {
        this.nickname = nickname;
        if (nickname != null) {
            System.out.println("Nickname is successfully changed");
        }
    }

    public String getName() {
        return  name;
    }

    public String getPassword() {
        return password;
    }

    public void changePassword(String oldPassword, String newPassword) {
        if (oldPassword.equals(password) && newPassword != null) {
            password = newPassword;
            System.out.println("Password is successfully changed");
        } else {
            System.out.println("Wrong information. Password is not changed");
        }
    }
}
