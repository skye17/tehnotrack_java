package ru.mail.track.Ermolaeva.tasks.messenger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mail.track.Ermolaeva.tasks.messenger.commands.CommandType;
import ru.mail.track.Ermolaeva.tasks.messenger.exceptions.IllegalCommandException;
import ru.mail.track.Ermolaeva.tasks.messenger.net.CommandMessage;

import java.util.Arrays;

// Class for transforming userInput into message.

public class InputHandler {
    public static final String PARAM_DELIMITER = "\\s+";
    static Logger log = LoggerFactory.getLogger(InputHandler.class);
    private ParserFactory parserFactory;

    public InputHandler(ParserFactory parserFactory) {
        this.parserFactory = parserFactory;
    }


    public CommandMessage processInput(String input) {
        String[] tokens = input.split(PARAM_DELIMITER);
        log.info("Tokens: {}", Arrays.toString(tokens));
        String command = tokens[0];
        int index = input.indexOf(command) + command.length();
        String argumentInput = input.substring(index).trim();
        if (command.startsWith("/")) {
            command = command.substring(1);
            try {
                CommandType commandType = CommandType.valueOf(command.toUpperCase());
                return parserFactory.getParser(commandType).apply(argumentInput);
            } catch (IllegalCommandException e) {
                throw new IllegalArgumentException("Invalid command usage: " + e.getMessage());
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Invalid command: " + command);
            }
        } else {
            throw new IllegalArgumentException("Command should start from /");
        }
    }
}
