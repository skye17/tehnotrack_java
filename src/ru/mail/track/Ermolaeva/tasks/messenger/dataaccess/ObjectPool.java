package ru.mail.track.Ermolaeva.tasks.messenger.dataaccess;

import ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.exceptions.ObjectAccessException;

public interface ObjectPool<Context> extends AutoCloseable {

    Context getInstance() throws ObjectAccessException;

    void releaseInstance(Context instance) throws ObjectAccessException;
}
