package ru.mail.track.Ermolaeva.tasks.messenger;

import java.io.IOException;

public interface AuthorizationService {
    void startAuthorization() throws IOException;

    User login(String username);

    User createUser(String username);

}
