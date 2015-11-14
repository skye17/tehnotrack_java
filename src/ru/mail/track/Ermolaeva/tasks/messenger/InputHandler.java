package ru.mail.track.Ermolaeva.tasks.messenger;

import ru.mail.track.Ermolaeva.tasks.messenger.commands.CommandType;
import ru.mail.track.Ermolaeva.tasks.messenger.commands.command_message.CommandMessage;


public class InputHandler {
    public static final String PARAM_DELIMITER = "\\s+";

    public CommandMessage processInput(String input) {
        String[] tokens = input.split(PARAM_DELIMITER);
        String command = tokens[0];
        int index = input.indexOf(command) + command.length();
        String argumentInput = input.substring(index).trim();
        if (command.startsWith("/")) {
            command = command.substring(1);
            try {
                CommandType commandType = CommandType.valueOf(command.toUpperCase());
                CommandMessage commandMessage = new CommandMessage(commandType);
                commandMessage.setInputString(argumentInput);
                return commandMessage;
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Invalid command: " + command);
            }
        } else {
            throw new IllegalArgumentException("Command should start from /");
        }
    }
}
