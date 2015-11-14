package ru.mail.track.Ermolaeva.tasks.messenger.dataaccess;

import ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.exceptions.DataAccessException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class SqlDaoFactory implements DaoFactory<Connection> {
    private String driver = "org.postgresql.Driver";
    private String url = "jdbc:postgresql://178.62.140.149:5432/skye17";
    private String user = "senthil";
    private String password = "ubuntu";
    private Connection connection;

    /**
     * Возвращает подключение к базе данных
     */

    public SqlDaoFactory() {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Connection getContext() throws DataAccessException {
        try {
            connection = DriverManager.getConnection(url, user, password);
            return connection;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }


    @Override
    public void close() throws DataAccessException {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new DataAccessException("Error closing connection");
            }
        }
    }
}
