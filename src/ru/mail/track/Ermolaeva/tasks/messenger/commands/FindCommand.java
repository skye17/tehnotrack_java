package ru.mail.track.Ermolaeva.tasks.messenger.commands;


import ru.mail.track.Ermolaeva.tasks.messenger.exceptions.ExitException;
import ru.mail.track.Ermolaeva.tasks.messenger.message.Message;
import ru.mail.track.Ermolaeva.tasks.messenger.session.Session;

import java.io.IOException;
import java.util.Collection;

public class FindCommand extends MessengerCommand {
    final String CASE_FLAG = "-c";

    public FindCommand(Session session) {
        super(session);
        name = "find";
        /*
         * Regex is framed in <> to distinguish with search by keyword.
         * Keyword is framed in "".
          */
        description = "/find <regex> [-c]- find messages, containing substring, matching with pattern\n" +
                "(if -c search is case-insensitive)\n" +
                "/find \"keyword\" - find messages, containing keyword";
    }

    @Override
    public CommandResult execute(String argString) {
        Collection<Message> result;
        String[] arguments = preprocessArgumentsString(argString);
        try {
            session.prepareMessageService();
        } catch (IOException e) {
            throw new ExitException("Can't read users' history: " + e.getMessage(), 1);
        }

        if (arguments.length == 1) {
            if (arguments[0].startsWith("\"") && arguments[0].endsWith("\"")) {
                String keyword = arguments[0].substring(1, arguments[0].length() - 1);
                result = session.getMessageService().getMessagesByKeyword(keyword);
            } else {
                String regex = arguments[0].substring(1, arguments[0].length() - 1);
                result = session.getMessageService().getMessagesByPattern(regex, false);
            }
        } else {
            String regex = arguments[0].substring(1, arguments[0].length() - 1);
            if (arguments[1].equals(CASE_FLAG)) {
                result = session.getMessageService().getMessagesByPattern(regex, true);
            } else {
                result = session.getMessageService().getMessagesByPattern(regex, false);
            }
        }

        if (result != null) {
            return out -> {
                for (Message message : result) {
                    out.write(message.getTimestamp().getBytes());
                    out.write(message.getMessage().getBytes());
                }
            };
        } else {
            return out -> out.write("No messages".getBytes());
        }
    }

    @Override
    protected String[] preprocessArgumentsString(String argsString) {
        String argument = argsString.trim();
        if (argument.startsWith("\"") && argument.endsWith("\"")) {
            return new String[]{argument};
        } else {
            if (argument.startsWith("<")) {
                int endIndex = argument.lastIndexOf(">");
                if (endIndex > 0) {
                    String search = argument.substring(0, endIndex + 1);
                    if (endIndex + 2 < argument.length()) {
                        String flag = argument.substring(endIndex + 2).trim();
                        return new String[]{search, flag};
                    } else {
                        return new String[]{search};
                    }
                } else {
                    illegalArgument("Second argument should be <regex>.");
                }
            } else {
                illegalArgument("Second argument should be \"keyword\" or <regex>.");
            }
        }
        return null;
    }

}
