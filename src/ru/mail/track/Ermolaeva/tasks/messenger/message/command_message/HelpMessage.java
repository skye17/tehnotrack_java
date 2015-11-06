package ru.mail.track.Ermolaeva.tasks.messenger.message.command_message;

import ru.mail.track.Ermolaeva.tasks.messenger.commands.CommandType;
import ru.mail.track.Ermolaeva.tasks.messenger.net.CommandMessage;

public class HelpMessage extends CommandMessage {
    private CommandType commandType;

    public HelpMessage() {
        super(CommandType.HELP);
    }

    public CommandType getCommand() {
        return null;
    }

    public void setCommand(CommandType commandType) {
        this.commandType = commandType;
    }
}
