package ru.mail.track.Ermolaeva.tasks.messenger.command_message;

import ru.mail.track.Ermolaeva.tasks.messenger.commands.CommandType;
import ru.mail.track.Ermolaeva.tasks.messenger.net.CommandMessage;


public class LogoutMessage extends CommandMessage {
    public LogoutMessage() {
        super(CommandType.LOGOUT);
    }
}
