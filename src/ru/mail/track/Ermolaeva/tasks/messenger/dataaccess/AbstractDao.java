package ru.mail.track.Ermolaeva.tasks.messenger.dataaccess;

import ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.exceptions.DataAccessException;
import ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.exceptions.IllegalDataStateException;

import java.sql.ResultSet;
import java.util.*;

// TODO: да, полезный класс получился
public abstract class AbstractDao<T extends Identified> implements GenericDao<T> {
    protected final static String ID_CLAUSE = " WHERE id= ?;";
    private final static String SELECT_ALL_QUERY = "SELECT * FROM ";
    protected String tableName;
    protected Map<Integer, String> columnNames;
    protected QueryExecutor queryExecutor;
    protected List<Integer> insertIndexes;
    protected List<Integer> updateIndexes;
    protected TableType tableType;

    public AbstractDao(QueryExecutor queryExecutor, TableType tableType) {
        this.queryExecutor = queryExecutor;
        this.tableType = tableType;
        TableProvider tableProvider = TableProvider.getInstance();
        this.tableName = tableProvider.getTableName(tableType);
        this.columnNames = tableProvider.getTableColumns(tableType);
        this.insertIndexes = new ArrayList<>(columnNames.keySet());
    }


    /*
     * Возвращает sql запрос для получения всех записей.
     */
    public String getSelectQuery() {
        return SELECT_ALL_QUERY + tableName;
    }


    public String getInsertQuery() {
        String insertQuery = "INSERT INTO " + tableName + " (";
        String values = "VALUES (";
        for (int index : insertIndexes) {
            String currentColumnName = columnNames.get(index);
            insertQuery += currentColumnName + ",";
            values += "?,";
        }
        insertQuery = insertQuery.substring(0, insertQuery.length() - 1);
        insertQuery += ") ";
        values = values.substring(0, values.length() - 1);
        values += ");";
        insertQuery += values;
        return insertQuery;
    }

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
    public T add(T item) throws DataAccessException {
        if (item.getId() != null) {
            throw new DataAccessException("Object already exists.");
        }

        String sql = getInsertQuery();
        Map<Integer, Object> values = prepareValuesForInsert(item);

        Long objectId = queryExecutor.executeUpdateReturningId(sql, values);
        if (objectId == null) {
            throw new IllegalDataStateException("Exception on new inserted record.");
        }
        item.setId(objectId);
        return item;

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


    public void setInsertIndexes(Integer... insertIndexes) {
        this.insertIndexes = null;
        this.insertIndexes = Arrays.asList(insertIndexes);
    }


    /**
     * Возвращает sql запрос для обновления записи.
     */
    public String getUpdateQuery() {
        String updateQuery = "UPDATE " + tableName + " SET ";
        for (int index : updateIndexes) {
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
    protected abstract Map<Integer, Object> prepareValuesForUpdate(T object)
            throws DataAccessException;


    @Override
    public void update(T object) throws DataAccessException {
        if (object.getId() == null) {
            throw new DataAccessException("No such object");
        }

        String sql = getUpdateQuery();
        Map<Integer, Object> values = prepareValuesForUpdate(object);

        int updated = queryExecutor.executeUpdate(sql, values);
        if (updated != 1) {
            throw new IllegalDataStateException("On update modified more than 1 record: " + updated);
        }
    }

    public void setUpdateIndexes(Integer... updateIndexes) {
        this.updateIndexes = null;
        this.updateIndexes = Arrays.asList(updateIndexes);
    }
}
