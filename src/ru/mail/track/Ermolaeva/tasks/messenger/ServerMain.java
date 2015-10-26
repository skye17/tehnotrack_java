package ru.mail.track.Ermolaeva.tasks.messenger;

import ru.mail.track.Ermolaeva.tasks.messenger.authorization.AuthorizationService;
import ru.mail.track.Ermolaeva.tasks.messenger.authorization.FileAuthorizationService;
import ru.mail.track.Ermolaeva.tasks.messenger.authorization.UserStore;
import ru.mail.track.Ermolaeva.tasks.messenger.message.MessageService;
import ru.mail.track.Ermolaeva.tasks.messenger.net.MainServer;

import java.io.IOException;

public class ServerMain {
    public static void main(String[] args) throws IOException {
        UserStore userStore = new UserStore();
        AuthorizationService fileAuthorizationService = new FileAuthorizationService(userStore, "records.txt", false);
        MessageService messageService = new MessageService("history", userStore);

        new MainServer().run(fileAuthorizationService, messageService);
    }
}
