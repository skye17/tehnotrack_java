package ru.mail.track.Ermolaeva.tasks.messenger;


import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        UserStore userStore = new UserStore();
        AuthorizationService fileAuthorizationService = new FileAuthorizationService(userStore, "records.txt", false);
        fileAuthorizationService.startAuthorization();
    }
}
