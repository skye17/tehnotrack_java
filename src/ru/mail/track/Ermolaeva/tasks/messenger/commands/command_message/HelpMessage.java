package ru.mail.track.Ermolaeva.tasks.messenger.commands.command_message;

import ru.mail.track.Ermolaeva.tasks.messenger.commands.CommandType;

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