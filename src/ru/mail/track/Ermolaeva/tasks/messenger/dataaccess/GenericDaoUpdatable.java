package ru.mail.track.Ermolaeva.tasks.messenger.dataaccess;


import ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.exceptions.DataAccessException;

public interface GenericDaoUpdatable<T extends Identified> extends GenericDao<T> {

    // FIXME: опять наружу вылезли какие-то индексы колонок, мне как пользователю вашего
    // кода, непонятно, что за индексы и какие колонки там скрываются на самом деле
    void update(T object, int[] columnIndexes) throws DataAccessException;
}
