package ru.mail.track.Ermolaeva.tasks.messenger.session;


public class DemoUser extends User {
    public DemoUser() {
        super("DemoUser", "");
    }

    @Override
    public boolean setPassword(String password) {
        return false;
    }

    @Override
    public boolean setNickname(String nickname) {
        return false;
    }

}
