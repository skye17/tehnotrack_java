package ru.mail.track.tasks.messenger;


public class Main {
    public static void main(String[] args) {
        // First variant.
        //Authorisation authorisation = new Authorisation();

        // Second variant.
        // Boolean indicates if we want to hide passwords or not. Doesn't work yet.
        Authorisation authorisation = new FileAuthorisation("records.txt", false);
        authorisation.run();
    }
}
