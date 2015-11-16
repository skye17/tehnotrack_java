package ru.mail.track.Ermolaeva.tasks.messenger.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mail.track.Ermolaeva.tasks.messenger.Interpreter;
import ru.mail.track.Ermolaeva.tasks.messenger.commands.*;
import ru.mail.track.Ermolaeva.tasks.messenger.commands.command_message.*;
import ru.mail.track.Ermolaeva.tasks.messenger.commands.exceptions.IllegalCommandException;
import ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.*;
import ru.mail.track.Ermolaeva.tasks.messenger.message.MessageStore;
import ru.mail.track.Ermolaeva.tasks.messenger.message.MessageStoreImpl;
import ru.mail.track.Ermolaeva.tasks.messenger.session.SessionManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

public class MainServer {
    public static final String PARAM_DELIMITER = "\\s+";
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

    private static String[] checkArgumentsNumber(String input, int argumentsNumber) {
        String[] tokens = input.split(PARAM_DELIMITER);
        if (tokens.length != argumentsNumber) {
            throw new IllegalCommandException("Wrong number of arguments: expected " + argumentsNumber
                    + ". Type /help <command> for more information");
        } else {
            return tokens;
        }
    }

    private static Map<CommandType, Command> getCommands(AbstractUserDao userStore,
                                                         MessageStore messageStore, SessionManager sessionManager) {
        Map<CommandType, Command> commands = new HashMap<>();
        Command loginCommand = new LoginCommand(userStore, sessionManager);
        loginCommand.setArgumentParser(argInput -> {
            String[] tokens = checkArgumentsNumber(argInput, 2);
            LoginMessage commandMessage = new LoginMessage();
            commandMessage.setLogin(tokens[0]);
            commandMessage.setPass(tokens[1]);
            return commandMessage;
        });
        commands.put(loginCommand.getType(), loginCommand);

        Command userCommand = new UserCommand(userStore);
        userCommand.setArgumentParser(argInput -> {
            String[] tokens = checkArgumentsNumber(argInput, 1);
            UserMessage commandMessage = new UserMessage();
            commandMessage.setNickname(tokens[0]);
            return commandMessage;
        });
        commands.put(userCommand.getType(), userCommand);

        Command userInfoCommand = new UserInfoCommand(userStore);
        userInfoCommand.setArgumentParser(argInput -> {
            UserInfoMessage commandMessage = new UserInfoMessage();
            if (argInput.equals("")) {
                return commandMessage;
            } else {
                try {
                    Long value = Long.valueOf(argInput);
                    commandMessage.setUserId(value);
                    return commandMessage;
                } catch (NumberFormatException ex) {
                    throw new IllegalCommandException("User id should be a number");
                }
            }
        });
        commands.put(userInfoCommand.getType(), userInfoCommand);

        Command userPassCommand = new UserPassCommand(userStore);
        userPassCommand.setArgumentParser(argInput -> {
            String[] tokens = checkArgumentsNumber(argInput, 2);
            UserPassMessage commandMessage = new UserPassMessage();
            commandMessage.setOldPassword(tokens[0]);
            commandMessage.setNewPassword(tokens[1]);
            return commandMessage;
        });
        commands.put(userPassCommand.getType(), userPassCommand);

        Command chatCreateCommand = new ChatCreateCommand(messageStore, userStore);
        chatCreateCommand.setArgumentParser(argInput -> {
            String[] tokens = checkArgumentsNumber(argInput, 1);
            ChatCreateMessage chatCreateMessage = new ChatCreateMessage();
            String idList = tokens[0];
            String[] ids = idList.split(",");
            List<Long> usersIdList = new ArrayList<>();
            for (String id : ids) {
                try {
                    Long value = Long.valueOf(id);
                    usersIdList.add(value);
                } catch (IllegalArgumentException io) {
                    throw new IllegalCommandException("User id should be a number");
                }
            }
            chatCreateMessage.setUserIdList(usersIdList);
            return chatCreateMessage;
        });
        commands.put(chatCreateCommand.getType(), chatCreateCommand);

        Command chatListCommand = new ChatListCommand(messageStore);
        chatListCommand.setArgumentParser(argInput -> {
            if (argInput.equals("")) {
                return new ChatCommandMessage(chatListCommand.getType());
            } else {
                throw new IllegalCommandException("Wrong number of arguments. " +
                        "Type /help chat_list for more information.");
            }
        });
        commands.put(chatListCommand.getType(), chatListCommand);


        Command chatHistoryCommand = new ChatHistoryCommand(messageStore);
        chatHistoryCommand.setArgumentParser(argInput -> {
            String[] tokens = checkArgumentsNumber(argInput, 1);
            try {
                Long value = Long.valueOf(tokens[0]);
                ChatCommandMessage chatCommandMessage = new ChatCommandMessage(CommandType.CHAT_HISTORY);
                chatCommandMessage.setChatId(value);
                return chatCommandMessage;
            } catch (NumberFormatException ex) {
                throw new IllegalCommandException("Chat id should be a number");
            }
        });
        commands.put(chatHistoryCommand.getType(), chatHistoryCommand);

        Command chatFindCommand = new ChatFindCommand(messageStore);
        chatFindCommand.setArgumentParser(argInput -> {
            String argument = argInput.trim();
            int whitespaceIndex = argument.indexOf(" ");
            if (whitespaceIndex < 0) {
                throw new IllegalCommandException("Type /help chat_find for more information");
            } else {
                String chatId = argument.substring(0, whitespaceIndex);
                try {
                    Long value = Long.valueOf(chatId);
                    argument = argument.substring(whitespaceIndex + 1).trim();
                    ChatFindMessage chatFindMessage = new ChatFindMessage();
                    chatFindMessage.setChatId(value);
                    chatFindMessage.setRegex(argument);
                    return chatFindMessage;
                } catch (NumberFormatException ex) {
                    throw new IllegalCommandException("Chat id should be a number");
                }
            }
        });
        commands.put(chatFindCommand.getType(), chatFindCommand);

        Command chatSendCommand = new ChatSendCommand(messageStore);
        chatSendCommand.setArgumentParser(argInput -> {
            String argument = argInput.trim();
            int whitespaceIndex = argument.indexOf(" ");
            if (whitespaceIndex < 0) {
                throw new IllegalCommandException("Type /help chat_send for more information");
            } else {
                String chatIdString = argument.substring(0, whitespaceIndex);
                try {
                    Long chatId = Long.valueOf(chatIdString);
                    argument = argument.substring(whitespaceIndex + 1).trim();
                    ChatSendMessage commandMessage = new ChatSendMessage();
                    commandMessage.setChatId(chatId);
                    commandMessage.setMessageToChat(argument);
                    return commandMessage;
                } catch (NumberFormatException ex) {
                    throw new IllegalCommandException("Chat id should be a number");
                }

            }
        });
        commands.put(chatSendCommand.getType(), chatSendCommand);

        Command chatJoinCommand = new ChatJoinCommand(messageStore);
        chatJoinCommand.setArgumentParser(argInput -> {
            String[] tokens = checkArgumentsNumber(argInput, 1);
            try {
                Long value = Long.valueOf(tokens[0]);
                ChatCommandMessage chatCommandMessage = new ChatCommandMessage(chatJoinCommand.getType());
                chatCommandMessage.setChatId(value);
                return chatCommandMessage;
            } catch (NumberFormatException ex) {
                throw new IllegalCommandException("Chat id should be a number");
            }
        });
        commands.put(chatJoinCommand.getType(), chatJoinCommand);

        Command chatLeaveCommand = new ChatLeaveCommand(messageStore);
        chatLeaveCommand.setArgumentParser(argInput -> {
            String[] tokens = checkArgumentsNumber(argInput, 1);
            try {
                Long value = Long.valueOf(tokens[0]);
                ChatCommandMessage chatCommandMessage = new ChatCommandMessage(chatLeaveCommand.getType());
                chatCommandMessage.setChatId(value);
                return chatCommandMessage;
            } catch (NumberFormatException ex) {
                throw new IllegalCommandException("Chat id should be a number");
            }
        });
        commands.put(chatLeaveCommand.getType(), chatLeaveCommand);

        Command chatTitleCommand = new ChatTitleCommand(messageStore);
        chatTitleCommand.setArgumentParser(argInput -> {
            String argument = argInput.trim();
            int whitespaceIndex = argument.indexOf(" ");
            if (whitespaceIndex < 0) {
                throw new IllegalCommandException("Type /help chat_title for more information");
            } else {
                String chatIdString = argument.substring(0, whitespaceIndex);
                try {
                    Long chatId = Long.valueOf(chatIdString);
                    argument = argument.substring(whitespaceIndex + 1).trim();
                    ChatTitleMessage commandMessage = new ChatTitleMessage();
                    commandMessage.setChatId(chatId);
                    commandMessage.setChatTitle(argument);
                    return commandMessage;
                } catch (NumberFormatException ex) {
                    throw new IllegalCommandException("Chat id should be a number");
                }
            }
        });
        commands.put(chatTitleCommand.getType(), chatTitleCommand);

        Command logoutCommand = new LogoutCommand(sessionManager);
        logoutCommand.setArgumentParser(argInput -> {
            checkArgumentsNumber(argInput, 1);
            return new CommandMessage(logoutCommand.getType());
        });
        commands.put(logoutCommand.getType(), logoutCommand);

        Command helpCommand = new HelpCommand(commands);
        helpCommand.setArgumentParser(argInput -> {
            HelpMessage commandMessage = new HelpMessage();
            if (argInput.equals("")) {
                return commandMessage;
            } else {
                try {
                    CommandType commandType = CommandType.valueOf(argInput.toUpperCase());
                    commandMessage.setCommand(commandType);
                    return commandMessage;
                } catch (IllegalArgumentException ex) {
                    throw new IllegalCommandException("Illegal command: " + argInput);
                }
            }
        });
        commands.put(helpCommand.getType(), helpCommand);

        return commands;
    }

    public static void main(String[] args) throws Exception {
        try (DaoFactory daoFactory = new SqlDaoFactory()) {
            QueryExecutor queryExecutor = new QueryExecutor((Connection) daoFactory.getContext());
            SessionManager sessionManager = new SessionManager();

            AbstractUserDao userStore = new UserStore(queryExecutor);
            MessageStore messageStore = new MessageStoreImpl(queryExecutor, sessionManager);

            Interpreter interpreter = new Interpreter(getCommands(userStore, messageStore, sessionManager));

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
