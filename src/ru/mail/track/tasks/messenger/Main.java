package ru.mail.track.tasks.messenger;


public class Main {
    public static void main(String[] args) {
        //Authorisation authorisation = new Authorisation();
        Authorisation authorisation = new FileAuthorisation("records.txt", false);
        authorisation.run();
    }
}
