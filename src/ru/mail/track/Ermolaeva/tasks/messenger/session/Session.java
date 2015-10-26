package ru.mail.track.Ermolaeva.tasks.messenger.session;

import ru.mail.track.Ermolaeva.tasks.messenger.message.MessageService;

import java.io.IOException;


public class Session {
    private User currentUser;
    private MessageService messageService;

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        currentUser = user;
    }

    public MessageService getMessageService() {
        return messageService;
    }

    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    public void close() throws IOException {
        messageService.close();
    }

    public void prepareMessageService() throws IOException {
        messageService.setCurrentUser(currentUser);
        if (!messageService.isLoaded(currentUser)) {
            messageService.loadHistory(currentUser.getName());
        }
    }

}
