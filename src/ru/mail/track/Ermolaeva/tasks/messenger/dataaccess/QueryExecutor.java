package ru.mail.track.Ermolaeva.tasks.messenger.dataaccess;

import ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.exceptions.DataAccessException;
import ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.exceptions.IllegalDataStateException;
import ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.exceptions.ObjectAccessException;

import java.sql.*;
import java.util.List;
import java.util.Map;

/**
 * Обертка для запроса в базу
 */
public class QueryExecutor {
    private ConnectionPool connectionPool;

    public QueryExecutor() {
        connectionPool = ConnectionPool.getPoolInstance();
    }

    // Простой запрос
    public <T> T executeQuery(String query, ResultHandler<T> handler) throws DataAccessException {
        try {
            Connection connection = connectionPool.getInstance();
            Statement stmt = connection.createStatement();
            stmt.executeQuery(query);
            ResultSet resultSet = stmt.getResultSet();
            T value = handler.handle(resultSet);
            resultSet.close();
            stmt.close();
            connectionPool.releaseInstance(connection);
            return value;
        } catch (SQLException | ObjectAccessException e) {
            throw new DataAccessException(e);
        }
    }

    // Подготовленный запрос
    public <T> T executeQuery(String query, Map<Integer, Object> args,
                              ResultHandler<T> handler) throws DataAccessException {
        try {
            Connection connection = connectionPool.getInstance();
            PreparedStatement stmt = connection.prepareStatement(query);
            for (Map.Entry<Integer, Object> entry : args.entrySet()) {
                stmt.setObject(entry.getKey(), entry.getValue());
            }
            ResultSet resultSet = stmt.executeQuery();
            T value = handler.handle(resultSet);
            resultSet.close();
            stmt.close();
            connectionPool.releaseInstance(connection);
            return value;
        } catch (SQLException | ObjectAccessException e) {
            throw new DataAccessException(e);
        }
    }

    // Update запросы
    public int[] executeUpdateBatch(String updateQuery, List<Map<Integer, Object>> args)
            throws DataAccessException {
        try {
            Connection connection = connectionPool.getInstance();
            PreparedStatement stmt = connection.prepareStatement(updateQuery);
            for (Map<Integer, Object> recordArgs : args) {
                for (Map.Entry<Integer, Object> entry : recordArgs.entrySet()) {
                    stmt.setObject(entry.getKey(), entry.getValue());
                }
                stmt.addBatch();
            }
            int[] results = stmt.executeBatch();
            stmt.close();
            connectionPool.releaseInstance(connection);
            return results;
        } catch (SQLException | ObjectAccessException e) {
            throw new DataAccessException(e);
        }
    }

    public int executeUpdate(String updateQuery) throws DataAccessException {
        try {
            Connection connection = connectionPool.getInstance();
            Statement stmt = connection.createStatement();
            int updated = stmt.executeUpdate(updateQuery);
            stmt.close();
            connectionPool.releaseInstance(connection);
            return updated;
        } catch (SQLException | ObjectAccessException e) {
            throw new DataAccessException(e);
        }
    }

    // Подготовленный запрос
    public int executeUpdate(String updateQuery, Map<Integer, Object> args) throws DataAccessException {
        try {
            Connection connection = connectionPool.getInstance();
            PreparedStatement stmt = connection.prepareStatement(updateQuery);
            for (Map.Entry<Integer, Object> entry : args.entrySet()) {
                stmt.setObject(entry.getKey(), entry.getValue());
            }
            int updated = stmt.executeUpdate();
            stmt.close();
            connectionPool.releaseInstance(connection);
            return updated;
        } catch (SQLException | ObjectAccessException e) {
            throw new DataAccessException(e);
        }
    }

    // Подготовленный запрос с возвращением id вставленной записи
    public Long executeUpdateReturningId(String updateQuery, Map<Integer, Object> args) throws DataAccessException {
        try {
            Connection connection = connectionPool.getInstance();
            PreparedStatement stmt = connection.prepareStatement(updateQuery, Statement.RETURN_GENERATED_KEYS);
            for (Map.Entry<Integer, Object> entry : args.entrySet()) {
                stmt.setObject(entry.getKey(), entry.getValue());
            }

            int updated = stmt.executeUpdate();
            if (updated != 1) {
                throw new IllegalDataStateException("On update modify more than 1 record: " + updated);
            }
            ResultSet rs = stmt.getGeneratedKeys();
            Long insertedId = null;
            while (rs.next()) {
                insertedId = rs.getLong(1);
            }
            rs.close();
            stmt.close();
            connectionPool.releaseInstance(connection);
            return insertedId;
        } catch (SQLException | ObjectAccessException e) {
            throw new DataAccessException(e);
        }
    }

}
