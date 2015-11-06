package ru.mail.track.Ermolaeva.tasks.messenger.authorization;

import ru.mail.track.Ermolaeva.tasks.messenger.session.User;

import java.io.IOException;

public interface AuthorizationService {
    User login(String username, String password);

    User createUser();

    boolean isLogin(User user);

    void saveAndClose() throws IOException;

    //void setOutputStream(PrintStream out);


    //void setInputStream(InputStream in);
}
