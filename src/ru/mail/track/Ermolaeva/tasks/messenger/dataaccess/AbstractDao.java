package ru.mail.track.Ermolaeva.tasks.messenger.dataaccess;

import ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.exceptions.DataAccessException;
import ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.exceptions.IllegalDataStateException;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class AbstractDao<T extends Identified> implements GenericDao<T> {
    protected final static String ID_CLAUSE = " WHERE id= ?;";
    private final static String SELECT_ALL_QUERY = "SELECT * FROM ";
    protected String tableName;
    protected Map<Integer, String> columnNames;
    protected QueryExecutor queryExecutor;

    public AbstractDao(QueryExecutor queryExecutor) {
        this.queryExecutor = queryExecutor;
    }

    public AbstractDao(QueryExecutor queryExecutor, String tableName) {
        this.queryExecutor = queryExecutor;
        this.tableName = tableName;
    }


    public void setColumnNames(Map<Integer, String> columnNames) {
        this.columnNames = columnNames;
    }

    public void setColumnNames(List<String> columnNamesList) {
        columnNames = new HashMap<>();
        if (columnNamesList != null && columnNamesList.size() != 0) {
            for (int i = 1; i <= columnNamesList.size(); ++i) {
                columnNames.put(i, columnNamesList.get(i - 1));
            }
        }
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }


    /*
     * Возвращает sql запрос для получения всех записей.
     */
    public String getSelectQuery() {
        return SELECT_ALL_QUERY + tableName;
    }


    public abstract String getInsertQuery();

    /*
     * Возвращает sql запрос для удаления записи из базы данных.
     */
    public String getDeleteQuery() {
        return "DELETE FROM " + tableName + ID_CLAUSE;
    }

    /**
     * Устанавливает аргументы insert запроса в соответствии со значением полей объекта object.
     */
    protected abstract Map<Integer, Object> prepareValuesForInsert(T object) throws DataAccessException;


    /**
     * Разбирает ResultSet и возвращает список объектов соответствующих содержимому ResultSet.
     */
    protected abstract List<T> parseResultSet(ResultSet rs);


    /**
     * Создает новую запись, соответствующую объекту object
     */
    @Override
    public T add(T object) throws DataAccessException {
        if (object.getId() != null) {
            throw new DataAccessException("Object already exists.");
        }

        String sql = getInsertQuery();
        Map<Integer, Object> values = prepareValuesForInsert(object);

        Long objectId = queryExecutor.executeUpdateReturningId(sql, values);
        if (objectId == null) {
            throw new IllegalDataStateException("Exception on new inserted record.");
        }
        object.setId(objectId);

        return object;
    }

    @Override
    public T getById(Long id) throws DataAccessException {
        if (id == null) {
            throw new DataAccessException("No such object");
        }
        String sql = getSelectQuery() + ID_CLAUSE;

        Map<Integer, Object> values = new HashMap<>();
        values.put(1, id);
        List<T> list = queryExecutor.executeQuery(sql, values, this::parseResultSet);
        if (list == null || list.size() == 0) {
            return null;
        }

        if (list.size() > 1) {
            throw new IllegalDataStateException("Received more than one record.");
        }

        return list.get(0);
    }


    /**
     * Удаляет запись об объекте из базы данных
     */
    @Override
    public void delete(T object) throws DataAccessException {
        if (object.getId() == null) {
            throw new DataAccessException("No such object");
        }

        String sql = getDeleteQuery();
        Map<Integer, Object> values = new HashMap<>();
        values.put(1, object.getId());
        int updated = queryExecutor.executeUpdate(sql, values);
        if (updated != 1) {
            throw new IllegalDataStateException("On delete modified more than 1 record: " + updated);
        }

    }

    /**
     * Возвращает список объектов соответствующих всем записям в базе данных
     */
    @Override
    public List<T> getAll() throws DataAccessException {
        String sql = getSelectQuery() + ID_CLAUSE;
        return queryExecutor.executeQuery(sql, this::parseResultSet);
    }


}
