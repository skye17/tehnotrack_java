package ru.mail.track.Ermolaeva.tasks.messenger;

public class Utility {
    public static void isNull(Object ... args){
        for (Object object:args) {
            if (object == null) {
                throw new IllegalArgumentException();
            }
        }

    }
}
