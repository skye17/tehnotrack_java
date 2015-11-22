package ru.mail.track.Ermolaeva.tasks.messenger.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mail.track.Ermolaeva.tasks.messenger.Interpreter;
import ru.mail.track.Ermolaeva.tasks.messenger.commands.Result;
import ru.mail.track.Ermolaeva.tasks.messenger.commands.command_message.CommandMessage;
import ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.QueryExecutor;
import ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.SqlDaoFactory;
import ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.TableProvider;
import ru.mail.track.Ermolaeva.tasks.messenger.message.MessageType;
import ru.mail.track.Ermolaeva.tasks.messenger.session.Session;
import ru.mail.track.Ermolaeva.tasks.messenger.session.SessionManager;
import ru.mail.track.Ermolaeva.tasks.messenger.setters.Commands;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class NioMainServer implements Runnable {

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

        TableProvider tableProvider = new TableProvider(queryExecutor);
        tableProvider.setUp();


        Protocol protocol = new SerializationProtocol();
        ObjectProtocol objectProtocol = new JsonProtocol();

        Interpreter interpreter = new Interpreter(Commands.getCommands(tableProvider, sessionManager,
                objectProtocol), objectProtocol);

        NioMainServer server = new NioMainServer();
        server.init(protocol, sessionManager, interpreter);
        System.out.println("Server started");
        Thread t = new Thread(server);
        t.start();
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
