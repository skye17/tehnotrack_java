package ru.mail.track.Ermolaeva.tasks.messenger.session;

import ru.mail.track.Ermolaeva.tasks.messenger.message.MessageService;

import java.io.IOException;


public class Session {
    private User currentUser;
    private MessageService messageService;

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public MessageService getMessageService() {
        return messageService;
    }

    public void setMessageService(MessageService messageService) throws IOException {
        this.messageService = messageService;
    }

    public void setupMessageService() {
        messageService.setCurrentUser(currentUser);
    }

    public void close() throws IOException {
        messageService.close();
    }

}
