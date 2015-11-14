package ru.mail.track.Ermolaeva.tasks.messenger.dataaccess;


import ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.exceptions.DataAccessException;

public interface DaoFactory<Context> extends AutoCloseable {
    /**
     * Возвращает подключение к базе данных
     */
    Context getContext() throws DataAccessException;
}
