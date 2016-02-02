package ru.mail.track.Ermolaeva.tasks.messenger.commands.command_message;

import ru.mail.track.Ermolaeva.tasks.messenger.commands.CommandType;

public class LoginMessage extends CommandMessage {
    private String login;
    private String password;

    public LoginMessage() {
        super(CommandType.LOGIN);
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPass(String password) {
        this.password = password;
    }
}
