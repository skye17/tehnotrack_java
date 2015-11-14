package ru.mail.track.Ermolaeva.tasks.messenger.dataaccess;


import ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.exceptions.DataAccessException;

import java.util.List;

/**
 * Интерфейс управления состоянием объектов.
 *
 * @param <T> тип объекта
 */

public interface GenericDao<T extends Identified> {

    /**
     * Создает новую запись в базе, соответствующую объекту object
     */
    T add(T object) throws DataAccessException;

    /**
     * Возвращает объект соответствующий записи с первичным ключом key или null
     */
    T getById(Long id) throws DataAccessException;


    /**
     * Удаляет запись об объекте из базы
     */
    void delete(T object) throws DataAccessException;

    /**
     * Возвращает список объектов соответствующих всем записям в базе
     */
    List<T> getAll() throws DataAccessException;
}
