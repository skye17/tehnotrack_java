package ru.mail.track.Ermolaeva.tasks.messenger.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mail.track.Ermolaeva.tasks.messenger.Interpreter;
import ru.mail.track.Ermolaeva.tasks.messenger.commands.*;
import ru.mail.track.Ermolaeva.tasks.messenger.message.MessageStore;
import ru.mail.track.Ermolaeva.tasks.messenger.message.MessageStoreImpl;
import ru.mail.track.Ermolaeva.tasks.messenger.session.SessionManager;
import ru.mail.track.Ermolaeva.tasks.messenger.session.Store;
import ru.mail.track.Ermolaeva.tasks.messenger.session.UserStore;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

public class MainServer {
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
        SessionManager sessionManager = new SessionManager();

        Store userStore = new UserStore();
        MessageStore messageStore = new MessageStoreImpl(sessionManager);


        Map<CommandType, Command> commands = new HashMap<>();
        commands.put(CommandType.LOGIN, new LoginCommand(userStore, sessionManager));
        commands.put(CommandType.USER, new UserCommand());
        commands.put(CommandType.USER_INFO, new UserInfoCommand(userStore));
        commands.put(CommandType.USER_PASS, new UserPassCommand(userStore));
        commands.put(CommandType.CHAT_CREATE, new ChatCreateCommand(messageStore, userStore));
        commands.put(CommandType.CHAT_LIST, new ChatListCommand(messageStore));
        commands.put(CommandType.CHAT_HISTORY, new ChatHistoryCommand(messageStore));
        commands.put(CommandType.CHAT_FIND, new ChatFindCommand(messageStore));
        commands.put(CommandType.CHAT_SEND, new ChatSendCommand(messageStore));
        commands.put(CommandType.LOGOUT, new LogoutCommand(sessionManager));
        commands.put(CommandType.HELP, new HelpCommand(commands));

        Interpreter interpreter = new Interpreter(commands);

        int port = 19000;
        int poolSize = 4;
        MainServer server = new MainServer(port, poolSize, sessionManager, interpreter);

        server.startServer();
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
