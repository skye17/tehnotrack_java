package ru.mail.track.Ermolaeva.tasks.messenger.authorization;

import ru.mail.track.Ermolaeva.tasks.messenger.session.User;

import java.io.IOException;

public interface AuthorizationService {
    User login(String username, String password);

    User createUser();

    void saveAndClose() throws IOException;
}
