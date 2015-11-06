package ru.mail.track.Ermolaeva.tasks.messenger.command_message;

import ru.mail.track.Ermolaeva.tasks.messenger.commands.CommandType;
import ru.mail.track.Ermolaeva.tasks.messenger.net.CommandMessage;

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
