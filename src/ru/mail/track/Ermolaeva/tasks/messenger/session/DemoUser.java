package ru.mail.track.Ermolaeva.tasks.messenger.session;


public class DemoUser extends User {
    public DemoUser() {
        super("DemoUser", "");
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
    }

    @Override
    public void changeNickname(String nickname) {
    }

}
