package ru.mail.track.Ermolaeva.tasks.messenger.commands;

import ru.mail.track.Ermolaeva.tasks.messenger.exceptions.ExitException;
import ru.mail.track.Ermolaeva.tasks.messenger.message.Message;
import ru.mail.track.Ermolaeva.tasks.messenger.session.Session;

import java.io.IOException;
import java.util.Collection;

public class HistoryCommand extends MessengerCommand {

    public HistoryCommand(Session session) {
        super(session);
        name = "history";
        description = "/history - show all messages\n/history <N> - show N latest messages";
    }

    @Override
    public void execute(String argString) {
        Collection<Message> result = null;
        String[] arguments = preprocessArgumentsString(argString);
        if (arguments.length == 0 || arguments.length == 1) {
            session.setupMessageService();
            if (!session.getMessageService().isLoaded()) {
                try {
                    session.getMessageService().loadHistory();
                } catch (IOException e) {
                    throw new ExitException("Can't read users' history: " + e.getMessage(), 1);
                }
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


        if (result != null) {
            for (Message message : result) {
                System.out.println(message.getTimestamp());
                System.out.println(message.getMessage());
            }
        }
    }

}
