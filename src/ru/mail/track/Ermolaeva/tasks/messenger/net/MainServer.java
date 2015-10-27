package ru.mail.track.Ermolaeva.tasks.messenger.net;

import ru.mail.track.Ermolaeva.tasks.messenger.authorization.AuthorizationService;
import ru.mail.track.Ermolaeva.tasks.messenger.exceptions.ExitException;
import ru.mail.track.Ermolaeva.tasks.messenger.message.MessageService;

import java.io.IOException;
import java.net.ServerSocket;

public class MainServer {
    public static final int PORT = 19000;

    public void run(AuthorizationService authorizationService, MessageService messageService) {
        try {
            int i = 0;
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server started");

            while (true) {
                new Thread(new Connection(i, serverSocket.accept(),
                        authorizationService, messageService)).start();
                i++;
            }
        } catch (ExitException | IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
