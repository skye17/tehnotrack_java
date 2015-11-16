package ru.mail.track.Ermolaeva.tasks.messenger.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mail.track.Ermolaeva.tasks.messenger.Interpreter;
import ru.mail.track.Ermolaeva.tasks.messenger.commands.*;
import ru.mail.track.Ermolaeva.tasks.messenger.commands.command_message.*;
import ru.mail.track.Ermolaeva.tasks.messenger.commands.exceptions.IllegalCommandException;
import ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.AbstractUserDao;
import ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.QueryExecutor;
import ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.SqlDaoFactory;
import ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.UserStore;
import ru.mail.track.Ermolaeva.tasks.messenger.message.MessageStore;
import ru.mail.track.Ermolaeva.tasks.messenger.message.MessageStoreImpl;
import ru.mail.track.Ermolaeva.tasks.messenger.message.MessageType;
import ru.mail.track.Ermolaeva.tasks.messenger.session.Session;
import ru.mail.track.Ermolaeva.tasks.messenger.session.SessionManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class NioMainServer implements Runnable {
    public static final String PARAM_DELIMITER = "\\s+";

    public static final int PORT = 19000;
    static Logger log = LoggerFactory.getLogger(NioMainServer.class);

    private Protocol protocol;
    private SessionManager sessionManager;
    private Interpreter interpreter;

    private Selector selector;
    private ServerSocketChannel socketChannel;

    private Map<Socket, Session> socketSessions;
    private Map<Socket, Result> socketResults;

    private ByteBuffer readBuffer = ByteBuffer.allocate(8192);

    private BlockingQueue<SocketEvent> eventQueue = new ArrayBlockingQueue<>(10);
    private ExecutorService service = Executors.newFixedThreadPool(5);


    public static void main(String[] args) throws Exception {
        QueryExecutor queryExecutor = new QueryExecutor(new SqlDaoFactory().getContext());
        SessionManager sessionManager = new SessionManager();

        AbstractUserDao userStore = new UserStore(queryExecutor);
        MessageStore messageStore = new MessageStoreImpl(queryExecutor, sessionManager);

        Interpreter interpreter = new Interpreter(getCommands(userStore, messageStore, sessionManager));

        Protocol protocol = new SerializationProtocol();
        NioMainServer server = new NioMainServer();
        server.init(protocol, sessionManager, interpreter);
        System.out.println("Server started");
        Thread t = new Thread(server);
        t.start();
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

    public void init(Protocol protocol, SessionManager sessionManager, Interpreter interpreter) throws Exception {
        selector = Selector.open();
        socketChannel = ServerSocketChannel.open();

        socketChannel.socket().bind(new InetSocketAddress(PORT));
        socketChannel.configureBlocking(false);

        socketChannel.register(selector, SelectionKey.OP_ACCEPT);

        this.protocol = protocol;
        this.sessionManager = sessionManager;
        this.interpreter = interpreter;
        socketSessions = new HashMap<>();
        socketResults = new HashMap<>();

        service.submit(() -> {
            try {
                while (true) {
                    SocketEvent event = eventQueue.take();
                    SocketChannel channel = event.getChannel();
                    ByteBuffer buffer = event.getBuffer();
                    Session session = socketSessions.get(channel.socket());

                    SocketMessage socketMessage = this.protocol.decode(buffer.array());

                    if (socketMessage != null && socketMessage.getMessageType().equals(MessageType.COMMAND)) {
                        Result result = this.interpreter.handleMessage(session, (CommandMessage) socketMessage);
                        if (!socketResults.containsKey(channel.socket())) {
                            socketResults.put(channel.socket(), result);
                        }
                    }

                    SelectionKey key = channel.keyFor(selector);
                    key.interestOps(SelectionKey.OP_WRITE);
                    selector.wakeup();
                }

            } catch (InterruptedException e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
        });

    }

    @Override
    public void run() {
        while (true) {
            try {
                log.info("On select()");
                int num = selector.select();
                log.info("selected...");
                if (num == 0) {
                    continue;
                }

                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> it = keys.iterator();
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    it.remove();
                    if (key.isAcceptable()) {
                        log.info("[acceptable] {}", key.hashCode());

                        SocketChannel socketChannel = ((ServerSocketChannel) key.channel()).accept();
                        Socket socket = socketChannel.socket();

                        socketChannel.configureBlocking(false);

                        Session session = sessionManager.createSession();
                        socketSessions.put(socket, session);
                        log.info("accepted on {}", socketChannel.getLocalAddress());

                        socketChannel.register(selector, SelectionKey.OP_READ);
                    } else if (key.isReadable()) {
                        log.info("[readable {}", key.hashCode());
                        SocketChannel socketChannel = (SocketChannel) key.channel();

                        readBuffer.clear();
                        int numRead;
                        try {
                            numRead = socketChannel.read(readBuffer);
                        } catch (IOException e) {
                            log.error("Failed to read data from channel", e);

                            // The remote forcibly closed the connection, cancel
                            // the selection key and close the channel.
                            key.cancel();
                            socketChannel.close();
                            break;
                        }

                        if (numRead == -1) {
                            log.error("Failed to read data from channel (-1)");

                            // Remote entity shut the socket down cleanly. Do the
                            // same from our end and cancel the channel.
                            key.channel().close();
                            key.cancel();
                            break;
                        }

                        log.info("read: {}", readBuffer.toString());
                        readBuffer.flip();

                        SocketEvent event = new SocketEvent();
                        event.setChannel(socketChannel);
                        event.setBuffer((ByteBuffer) readBuffer.flip());
                        try {
                            eventQueue.put(event);
                        } catch (InterruptedException e) {
                            System.err.println(e.getMessage());
                            System.exit(1);
                        }

                    } else if (key.isWritable()) {
                        log.info("[writable]{}", key.hashCode());
                        SocketChannel socketChannel = (SocketChannel) key.channel();

                        if (socketResults.containsKey(socketChannel.socket())) {
                            SocketMessage message = socketResults.get(socketChannel.socket()).getMessage();
                            byte[] encodedMessage = protocol.encode(message);
                            if (encodedMessage != null) {
                                ByteBuffer buf = ByteBuffer.wrap(encodedMessage);
                                socketChannel.write(buf);
                                buf = null;
                            }
                            socketResults.remove(socketChannel.socket());
                        }
                        key.interestOps(SelectionKey.OP_READ);
                    }
                }
                keys.clear();
            } catch (IOException e) {
                System.exit(1);
            }
        }
    }
}