package ru.mail.track.Ermolaeva.tasks.messenger.commands;

import ru.mail.track.Ermolaeva.tasks.messenger.exceptions.ExitException;
import ru.mail.track.Ermolaeva.tasks.messenger.message.Message;
import ru.mail.track.Ermolaeva.tasks.messenger.session.Session;

import java.io.IOException;
import java.util.Collection;

public class HistoryCommand extends MessengerCommand {
    Collection<Message> result;

    public HistoryCommand(Session session) {
        super(session);
        name = "history";
        description = "/history - show all messages\n/history <N> - show N latest messages";
    }

    @Override
    public CommandResult execute(String argString) {
        String[] arguments = preprocessArgumentsString(argString);
        if (arguments.length == 0 || arguments.length == 1) {
            try {
                session.prepareMessageService();
            } catch (IOException e) {
                throw new ExitException("Can't read users' history: " + e.getMessage(), 1);
            }

            if (arguments.length == 0) {
                result = session.getMessageService().getMessageHistory();
            } else {
                try {
                    int messagesNumber = Integer.parseInt(arguments[0]);
                    result = session.getMessageService().getMessageHistory(messagesNumber);
                } catch (NumberFormatException n) {
                    illegalArgument("Second argument should be number.");
                }
            }
        } else {
            illegalArgument();
        }

        if (result == null || result.size() == 0) {
            return out -> out.write("No messages".getBytes());
        } else {
            return out -> {
                for (Message message : result) {
                    out.write(message.getTimestamp().getBytes());
                    out.write(message.getMessage().getBytes());
                }
            };
        }
    }

}
