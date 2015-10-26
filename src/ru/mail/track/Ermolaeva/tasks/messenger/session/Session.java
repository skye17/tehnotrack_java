package ru.mail.track.Ermolaeva.tasks.messenger.session;

import ru.mail.track.Ermolaeva.tasks.messenger.message.UserMessageService;

import java.io.IOException;


public class Session {
    private User currentUser;
    private UserMessageService userMessageService;

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        currentUser = user;
    }

    public UserMessageService getMessageService() {
        return userMessageService;
    }

    public void setMessageService(UserMessageService messageService) {
        this.userMessageService = messageService;
    }

    public void close() throws IOException {
        userMessageService.close();
    }

    public void prepareMessageService() throws IOException {
        userMessageService.setCurrentUser(currentUser);
        if (!userMessageService.isLoaded(currentUser)) {
            userMessageService.loadHistory(currentUser.getName());
        }
    }

}
