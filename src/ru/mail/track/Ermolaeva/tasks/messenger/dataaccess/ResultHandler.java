package ru.mail.track.Ermolaeva.tasks.messenger.dataaccess;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Обобщенный интерфейс обработки результата
 */

public interface ResultHandler<T> {
    T handle(ResultSet resultSet) throws SQLException;
}
