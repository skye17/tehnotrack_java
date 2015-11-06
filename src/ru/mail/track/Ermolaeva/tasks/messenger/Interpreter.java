package ru.mail.track.Ermolaeva.tasks.messenger;


import ru.mail.track.Ermolaeva.tasks.messenger.commands.Command;
import ru.mail.track.Ermolaeva.tasks.messenger.commands.CommandResult;
import ru.mail.track.Ermolaeva.tasks.messenger.commands.CommandType;
import ru.mail.track.Ermolaeva.tasks.messenger.commands.Result;
import ru.mail.track.Ermolaeva.tasks.messenger.message.MessageType;
import ru.mail.track.Ermolaeva.tasks.messenger.net.CommandMessage;
import ru.mail.track.Ermolaeva.tasks.messenger.net.MessageListener;
import ru.mail.track.Ermolaeva.tasks.messenger.net.SocketMessage;
import ru.mail.track.Ermolaeva.tasks.messenger.session.Session;

import java.io.IOException;
import java.util.Map;


public class Interpreter implements MessageListener {

    private Map<CommandType, Command> commands;

    public Interpreter(Map<CommandType, Command> commands) {
        this.commands = commands;
    }


    public Result handleMessage(Session session, CommandMessage message) {
        if (commands.containsKey(message.getCommandType())) {
            return commands.get(message.getCommandType()).execute(session, message);
        } else {
            return new CommandResult("Invalid command", true);
        }
    }


    @Override
    public void update(Session session, SocketMessage message) {
        if (message.getMessageType().equals(MessageType.COMMAND)) {
            Result commandResult = handleMessage(session, (CommandMessage) message);
            try {
                session.getConnectionHandler().send(commandResult.getMessage());
            } catch (IOException io) {
            }
        }
    }
}
