package ru.mail.track.Ermolaeva.tasks.messenger.net;


import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mail.track.Ermolaeva.tasks.messenger.InputHandler;
import ru.mail.track.Ermolaeva.tasks.messenger.commands.CommandType;
import ru.mail.track.Ermolaeva.tasks.messenger.commands.command_message.CommandMessage;
import ru.mail.track.Ermolaeva.tasks.messenger.message.MessageType;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class NioClient {

    public static final int PORT = 19000;
    public static final String HOST = "localhost";
    static Logger log = LoggerFactory.getLogger(NioClient.class);
    private Protocol protocol;

    private Selector selector;
    private SocketChannel channel;
    private ByteBuffer buffer = ByteBuffer.allocate(8192);
    private BlockingQueue<CommandMessage> inputQueue = new ArrayBlockingQueue<>(10);

    public NioClient(Protocol protocol) {
        this.protocol = protocol;
    }

    public static void main(String[] args) throws Exception {
        Protocol protocol = new SerializationProtocol();
        NioClient client = new NioClient(protocol);
        client.start();
    }

    private void start() throws IOException, InterruptedException {
        Thread consoleHandler = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            InputHandler inputHandler = new InputHandler();
            System.out.println("$");
            while (true) {
                try {
                    if (scanner.hasNextLine()) {
                        String input = scanner.nextLine();
                        if ("/exit".equals(input)) {
                            inputQueue.put(new CommandMessage(CommandType.LOGOUT));
                            System.exit(0);
                        }

                        try {
                            inputQueue.put(inputHandler.processInput(input));
                        } catch (IllegalArgumentException ex) {
                            System.out.println(ex.getMessage());
                        }

                    }

                    SelectionKey key = channel.keyFor(selector);
                    log.info("wake up: {}", key.hashCode());
                    key.interestOps(SelectionKey.OP_WRITE);
                    selector.wakeup();
                } catch (InterruptedException e) {
                    System.err.println(e.getMessage());
                    System.exit(1);
                }
            }
        });

        consoleHandler.start();

        selector = Selector.open();
        channel = SocketChannel.open();
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_CONNECT);

        channel.connect(new InetSocketAddress(HOST, PORT));

        while (true) {
            log.info("Waiting on select()...");
            int num = selector.select();
            log.info("Raised {} events", num);


            Set<SelectionKey> keys = selector.selectedKeys();
            for (SelectionKey sKey : keys) {
                if (sKey.isConnectable()) {
                    channel.finishConnect();
                    sKey.interestOps(SelectionKey.OP_WRITE);
                } else if (sKey.isReadable()) {
                    log.info("[readable]");

                    buffer.clear();
                    int numRead = channel.read(buffer);
                    if (numRead < 0) {
                        break;
                    }

                    SocketMessage serverResponse = protocol.decode(buffer.array());
                    processServerResponse(serverResponse);

                } else if (sKey.isWritable()) {
                    log.info("[writable]");
                    CommandMessage message = inputQueue.poll();
                    if (message != null) {
                        byte[] encodedMessage = protocol.encode(message);
                        if (encodedMessage != null) {
                            channel.write(ByteBuffer.wrap(encodedMessage));
                        }
                    }

                    sKey.interestOps(SelectionKey.OP_READ);
                }
            }
        }
    }

    private void processServerResponse(SocketMessage serverResponse) {
        if (serverResponse != null && serverResponse.getMessageType().equals(MessageType.RESPONSE)) {
            ResponseMessage responseMessage = (ResponseMessage) serverResponse;
            ObjectMapper mapper = new ObjectMapper();
            Object object;
            try {
                Class objectClass = Class.forName(responseMessage.getResultClass());
                object = mapper.readValue(responseMessage.getResponse(), objectClass);
                if (responseMessage.getStatus()) {
                    System.out.println("Error!");
                }
                System.out.printf("%s\n", object);
                System.out.println("$");
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Can't decode server response: " + e.getMessage());
            }
        } else {
            System.out.println("Wrong server response");
        }
    }
}
