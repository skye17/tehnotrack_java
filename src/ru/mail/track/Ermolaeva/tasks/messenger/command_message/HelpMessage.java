package ru.mail.track.Ermolaeva.tasks.messenger.command_message;

import ru.mail.track.Ermolaeva.tasks.messenger.commands.CommandType;
import ru.mail.track.Ermolaeva.tasks.messenger.net.CommandMessage;

public class HelpMessage extends CommandMessage {
    private CommandType queryCommandType;

    public HelpMessage() {
        super(CommandType.HELP);
    }

    public CommandType getCommand() {
        return queryCommandType;
    }

    public void setCommand(CommandType commandType) {
        this.queryCommandType = commandType;
    }
}