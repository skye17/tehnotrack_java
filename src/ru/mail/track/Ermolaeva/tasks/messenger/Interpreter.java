package ru.mail.track.Ermolaeva.tasks.messenger;

import ru.mail.track.Ermolaeva.tasks.messenger.commands.Command;
import ru.mail.track.Ermolaeva.tasks.messenger.exceptions.ExitException;
import ru.mail.track.Ermolaeva.tasks.messenger.exceptions.IllegalCommandException;
import ru.mail.track.Ermolaeva.tasks.messenger.session.Session;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.*;

public class Interpreter {
    public static final String PROMPT = "$ ";

    public static final String COMMAND_NOT_FOUND_MSG = "Command not found: ";
    public static final String EXIT_COMMAND = "/exit";

    private final Map<String, Command> commands;

    private InputStream in;
    private PrintStream out;
    private Session session;

    public Interpreter(Session session, ArrayList<Command> commands,
                       final InputStream inStream,
                       final PrintStream outStream) {
        if (inStream == null || outStream == null) {
            throw new ExitException("Given streams are not valid", 1);
        }
        in = inStream;
        out = outStream;
        this.session = session;
        this.commands = new HashMap<>();
        for (Command command : commands) {
            this.commands.put(command.getName(), command);
        }
    }

    public Interpreter(Session session, final ArrayList<Command> commands) {
        this(session, commands, System.in, System.out);
    }


    public final void run() {
        out.println("Welcome to the messenger! Type /help to see all available commands");
        out.println("To start working you need to login. Type /help login to get more information");
        userMode();
    }

    private void userMode() throws ExitException {
        try (Scanner scan = new Scanner(in)) {
            while (true) {
                out.print(PROMPT);
                String line = null;
                try {
                    line = scan.nextLine();
                } catch (NoSuchElementException e) {
                    commands.get(EXIT_COMMAND).execute(null);
                }
                try {
                    if (line != null && line.startsWith("/")) {
                        commandHandler(line.substring(1));
                    } else {
                        session.setupMessageService();
                        try {
                            if (!session.getMessageService().isLoaded()) {
                                session.getMessageService().loadHistory();
                            }
                            session.getMessageService().addMessage(line);
                        } catch (IOException io) {
                            out.println("Can't read users' history:" + io.getMessage());
                            commands.get(EXIT_COMMAND).execute(null);
                        }
                    }
                } catch (IllegalCommandException e) {
                    out.println(e.getMessage());
                } catch (ExitException ex) {
                    out.println(ex.getMessage());
                    throw ex;
                }
            }
        }
    }

    private void commandHandler(String cmd) throws ExitException {
        int whitespaceIndex = cmd.indexOf(" ");
        String commandName;
        String arguments;
        if (whitespaceIndex < 0) {
            commandName = cmd;
            arguments = null;
        } else {
            commandName = cmd.substring(0, whitespaceIndex);
            arguments = cmd.substring(whitespaceIndex + 1);
        }
        Command command = commands.get(commandName);
        if (command == null) {
            throw new IllegalCommandException(COMMAND_NOT_FOUND_MSG + commandName);
        } else {
            try {
                command.execute(arguments);
            } catch (IllegalArgumentException a) {
                out.println(a.getMessage());
            }
        }
    }
}
