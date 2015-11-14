package ru.mail.track.Ermolaeva.tasks.messenger.dataaccess;


import ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.exceptions.DataAccessException;

public interface GenericDaoUpdatable<T extends Identified> extends GenericDao<T> {
    void update(T object, int[] columnIndexes) throws DataAccessException;
}
