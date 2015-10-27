package ru.mail.track.Ermolaeva.tasks.messenger.net;

import ru.mail.track.Ermolaeva.tasks.messenger.Interpreter;
import ru.mail.track.Ermolaeva.tasks.messenger.authorization.AuthorizationService;
import ru.mail.track.Ermolaeva.tasks.messenger.commands.*;
import ru.mail.track.Ermolaeva.tasks.messenger.message.MessageService;
import ru.mail.track.Ermolaeva.tasks.messenger.session.DemoUser;
import ru.mail.track.Ermolaeva.tasks.messenger.session.Session;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Connection implements Runnable {
    private Socket clientSocket;
    private int id;

    private AuthorizationService authorizationService;
    private MessageService messageService;

    public Connection(int id, Socket socket, AuthorizationService authorizationService, MessageService messageService) {
        this.id = id;
        this.clientSocket = socket;
        this.authorizationService = authorizationService;
        this.messageService = messageService;
    }

    @Override
    public void run() {
        Session session = new Session();
        session.setCurrentUser(new DemoUser());

        session.setMessageService(messageService);
        try {
            List<Command> commandsList = new ArrayList<>();
            commandsList.add(new LoginCommand(session, authorizationService));
            commandsList.add(new FindCommand(session));
            commandsList.add(new HistoryCommand(session));
            commandsList.add(new UserCommand(session));
            commandsList.add(new ExitCommand(authorizationService, session));
            HelpCommand helpCommand = new HelpCommand(commandsList);
            commandsList.add(helpCommand);
            try (InputStream in = clientSocket.getInputStream();
                 OutputStream out = clientSocket.getOutputStream()) {
                authorizationService.setInputStream(in);
                PrintStream outPrintStream = new PrintStream(out);
                authorizationService.setOutputStream(outPrintStream);
                new Interpreter(session, commandsList, in, outPrintStream).run();
                clientSocket.close();

            }
        } catch (IOException io) {
            //
        }
    }
}
