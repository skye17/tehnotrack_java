package ru.mail.track.Ermolaeva.tasks.messenger.dataaccess;

import ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.exceptions.DataAccessException;
import ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.exceptions.IllegalDataStateException;

import java.util.Map;

public abstract class AbstractDaoUpdatable<T extends Identified> extends AbstractDao<T>
        implements GenericDaoUpdatable<T> {

    public AbstractDaoUpdatable(QueryExecutor queryExecutor, String tableName) {
        super(queryExecutor, tableName);
    }

    /**
     * Возвращает sql запрос для обновления записи.
     */
    public String getUpdateQuery(int[] columnIndexes) {
        String updateQuery = "UPDATE " + tableName + " SET ";
        for (int index : columnIndexes) {
            String currentColumnName = columnNames.get(index);
            updateQuery += currentColumnName + " = ?,";
        }
        updateQuery = updateQuery.substring(0, updateQuery.length() - 1);

        updateQuery += ID_CLAUSE;
        return updateQuery;
    }

    /**
     * Устанавливает аргументы update запроса в соответствии со значением полей объекта object.
     */
    protected abstract Map<Integer, Object> prepareValuesForUpdate(T object, int[] columnIndexes) throws DataAccessException;


    @Override
    public void update(T object, int[] columnIndexes) throws DataAccessException {
        if (object.getId() == null) {
            throw new DataAccessException("No such object");
        }

        String sql = getUpdateQuery(columnIndexes);
        Map<Integer, Object> values = prepareValuesForUpdate(object, columnIndexes);

        int updated = queryExecutor.executeUpdate(sql, values);
        if (updated != 1) {
            throw new IllegalDataStateException("On update modified more than 1 record: " + updated);
        }
    }

}

