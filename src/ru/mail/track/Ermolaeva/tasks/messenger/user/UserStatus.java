package ru.mail.track.Ermolaeva.tasks.messenger.user;

public class UserStatus {
    private User user;
    private boolean isExist;

    public UserStatus(User user, boolean isExist) {
        this.user = user;
        this.isExist = isExist;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isExist() {
        return isExist;
    }

    public void setIsExist(boolean isExist) {
        this.isExist = isExist;
    }
}
