package ru.mail.track.Ermolaeva.tasks.messenger.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mail.track.Ermolaeva.tasks.messenger.Interpreter;
import ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.DaoFactory;
import ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.QueryExecutor;
import ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.SqlDaoFactory;
import ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.TableProvider;
import ru.mail.track.Ermolaeva.tasks.messenger.session.SessionManager;
import ru.mail.track.Ermolaeva.tasks.messenger.setters.Commands;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

public class MainServer {

    public static final int PORT = 19000;
    public static final int POOLSIZE = 4;
    static Logger log = LoggerFactory.getLogger(MainServer.class);

    private final ServerSocket serverSocket;
    private final ExecutorService pool;

    private AtomicLong internalCounter = new AtomicLong(0);
    private Map<Long, ConnectionHandler> handlers = new HashMap<>();
    private SessionManager sessionManager;
    private volatile boolean isRunning;

    private Interpreter interpreter;

    public MainServer(int port, int poolSize,
                      SessionManager sessionManager, Interpreter interpreter)
            throws IOException {
        serverSocket = new ServerSocket(port);
        pool = Executors.newFixedThreadPool(poolSize);
        System.out.println("Server started");
        this.sessionManager = sessionManager;
        this.interpreter = interpreter;
    }

    public static void main(String[] args) throws Exception {
        try (DaoFactory daoFactory = new SqlDaoFactory()) {
            QueryExecutor queryExecutor = new QueryExecutor((Connection) daoFactory.getContext());
            SessionManager sessionManager = new SessionManager();

            TableProvider tableProvider = new TableProvider(queryExecutor);
            tableProvider.setUp();

            ObjectProtocol objectProtocol = new JsonProtocol();
            Interpreter interpreter = new Interpreter(Commands.getCommands(tableProvider, sessionManager,
                    objectProtocol), objectProtocol);

            MainServer server = new MainServer(PORT, POOLSIZE, sessionManager, interpreter);

            server.startServer();
        }
    }

    public void startServer() {
        isRunning = true;
        try {
            while (isRunning) {
                Socket socket = serverSocket.accept();
                log.info("Accepted. " + socket.getInetAddress());

                ConnectionHandler handler = new SocketConnectionHandler(sessionManager.createSession(), socket);
                handler.addListener(interpreter);

                handlers.put(internalCounter.incrementAndGet(), handler);
                pool.submit(handler);
            }
        } catch (Exception ex) {
            stopServer();
            pool.shutdown();
        }
    }

    public void stopServer() {
        isRunning = false;
        handlers.values().forEach(ConnectionHandler::stop);
    }
}
