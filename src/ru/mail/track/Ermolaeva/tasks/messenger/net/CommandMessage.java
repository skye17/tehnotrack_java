package ru.mail.track.Ermolaeva.tasks.messenger.net;

import ru.mail.track.Ermolaeva.tasks.messenger.commands.CommandType;
import ru.mail.track.Ermolaeva.tasks.messenger.message.MessageType;

public class CommandMessage extends SocketMessage {
    private CommandType commandType;

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

}

