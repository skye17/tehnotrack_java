package ru.mail.track.Ermolaeva.tasks.messenger.dataaccess;

import ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.exceptions.ObjectAccessException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ConnectionPool implements ObjectPool<Connection> {
    private volatile static ConnectionPool uniquePool;
    private String driver = "org.postgresql.Driver";
    private String url = "jdbc:postgresql://178.62.140.149:5432/skye17";
    private String user = "senthil";
    private String password = "ubuntu";
    private Queue<Connection> freeConnections;
    private Queue<Connection> busyConnections;

    private ConnectionPool() {
        try {
            Class.forName(driver);
            freeConnections = new ConcurrentLinkedQueue<>();
            busyConnections = new ConcurrentLinkedQueue<>();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static ConnectionPool getPoolInstance() {
        if (uniquePool == null) {
            synchronized (ConnectionPool.class) {
                if (uniquePool == null) {
                    uniquePool = new ConnectionPool();
                }
            }
        }
        return uniquePool;
    }

    @Override
    public Connection getInstance() throws ObjectAccessException {
        try {
            Connection connection = freeConnections.poll();
            if (connection == null) {
                connection = DriverManager.getConnection(url, user, password);
            }
            busyConnections.add(connection);
            return connection;
        } catch (SQLException e) {
            throw new ObjectAccessException("Can't create new connection:" + e.getMessage());
        }
    }

    @Override
    public void releaseInstance(Connection connection) throws ObjectAccessException {
        if (!busyConnections.remove(connection)) {
            throw new ObjectAccessException("Connection is not found in the pool");
        }
        if (!freeConnections.add(connection)) {
            throw new ObjectAccessException("Can't add connection to the connection pool");
        }
    }

    @Override
    public void close() {
        try {
            for (Connection connection : freeConnections) {
                connection.close();
            }
            for (Connection connection : busyConnections) {
                connection.close();
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
