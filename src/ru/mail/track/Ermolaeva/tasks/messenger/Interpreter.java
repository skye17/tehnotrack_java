package ru.mail.track.Ermolaeva.tasks.messenger;

import ru.mail.track.Ermolaeva.tasks.messenger.commands.Command;
import ru.mail.track.Ermolaeva.tasks.messenger.commands.CommandResult;
import ru.mail.track.Ermolaeva.tasks.messenger.exceptions.ExitException;
import ru.mail.track.Ermolaeva.tasks.messenger.exceptions.IllegalCommandException;
import ru.mail.track.Ermolaeva.tasks.messenger.exceptions.StreamException;
import ru.mail.track.Ermolaeva.tasks.messenger.session.Session;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Interpreter {
    public static final String COMMAND_NOT_FOUND_MSG = "Command not found: ";
    public static final String EXIT_COMMAND = "/exit";
    public static final String STREAM_EXCEPTION_MSG = "Can't write to given outstream";

    private final Map<String, Command> commands;

    private InputStream in;
    private PrintStream out;

    private Session session;

    public Interpreter(Session session, List<Command> commandsList,
                       final InputStream inStream,
                       final PrintStream outStream) {
        if (inStream == null || outStream == null) {
            throw new ExitException("Given streams are not valid", 1);
        }
        in = inStream;
        out = outStream;
        this.session = session;
        commands = new HashMap<>();
        for (Command command : commandsList) {
            commands.put(command.getName(), command);
        }
    }

    public Interpreter(Session session, final List<Command> commandsList) {
        this(session, commandsList, System.in, System.out);
    }

    public final void run() {
        try {
            userMode();
        } catch (IOException io) {
            throw new ExitException(io.getMessage(), 1);
        }
    }

    void send(String msg) throws IOException {
        out.write(msg.getBytes());
    }

    void onMessage(String msg) {
        System.out.println("Client:" + msg);
    }

    private void userMode() throws IOException {
        try (PrintStream printStream =
                     new PrintStream(out, true)) {
            printStream.write("Welcome to the messenger!".getBytes());
            byte[] buf = new byte[512 * 1024];
            while (true) {
                int readBytes = in.read(buf);
                String line = null;
                if (readBytes >= 0) {
                    line = new String(buf, 0, readBytes);
                    onMessage(line);
                }
                try {
                    if (line != null && line.startsWith("/")) {
                        commandHandler(line.substring(1));
                    } else {
                        try {
                            session.prepareMessageService();
                            session.getMessageService().addMessage(line);
                            send("OK");
                        } catch (IOException io) {
                            send("Can't read users' history:" + io.getMessage());
                            commands.get(EXIT_COMMAND).execute(null);
                        }
                    }
                } catch (IllegalCommandException e) {
                    printStream.write(e.getMessage().getBytes());
                } catch (ExitException ex) {
                    printStream.write(ex.getMessage().getBytes());
                    return;
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
                CommandResult result = command.execute(arguments);
                if (result != null) {
                    result.show(out);
                }
            } catch (IllegalArgumentException a) {
                out.println(a.getMessage());
            } catch (IOException io) {
                throw new StreamException(STREAM_EXCEPTION_MSG + io.getMessage());
            }
        }
    }
}
