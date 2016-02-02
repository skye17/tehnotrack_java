package ru.mail.track.Ermolaeva.tasks.messenger.commands.command_message;

import ru.mail.track.Ermolaeva.tasks.messenger.commands.CommandType;
import ru.mail.track.Ermolaeva.tasks.messenger.message.MessageType;
import ru.mail.track.Ermolaeva.tasks.messenger.net.SocketMessage;

public class CommandMessage extends SocketMessage {
    private CommandType commandType;
    private String inputString;

    public CommandMessage() {
        setMessageType(MessageType.COMMAND);
    }

    public CommandMessage(CommandType commandType) {
        this();
        this.commandType = commandType;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public void setCommandType(CommandType commandType) {
        this.commandType = commandType;
    }


    public String getInputString() {
        return inputString;
    }

    public void setInputString(String inputString) {
        this.inputString = inputString;
    }
}

