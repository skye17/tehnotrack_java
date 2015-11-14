package ru.mail.track.Ermolaeva.tasks.messenger.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mail.track.Ermolaeva.tasks.messenger.session.Session;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс работающий с сокетом, умеет отправлять данные в сокет
 * Также слушает сокет и рассылает событие о сообщении всем подписчикам (асинхронность)
 */
public class SocketConnectionHandler implements ConnectionHandler {
    static Logger log = LoggerFactory.getLogger(SocketConnectionHandler.class);
    private List<MessageListener> listeners = new ArrayList<>();
    private Socket socket;
    private InputStream in;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private Session session;

    public SocketConnectionHandler(Session session, Socket socket) throws IOException {
        this(socket);
        this.session = session;
        session.setConnectionHandler(this);
    }

    public SocketConnectionHandler(Socket socket) throws IOException {
        this.socket = socket;
        in = socket.getInputStream();
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
    }

    @Override
    public void send(SocketMessage message) throws IOException {
        if (log.isDebugEnabled()) {
            log.debug(message.toString());
        }
        objectOutputStream.writeObject(message);
        objectOutputStream.flush();
    }

    @Override
    public void addListener(MessageListener listener) {
        listeners.add(listener);
    }

    @Override
    public void notifyListeners(Session session, SocketMessage msg) {
        listeners.forEach(it -> it.update(session, msg));
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                if (in.available() > 0) {
                    if (objectInputStream == null) {
                        objectInputStream = new ObjectInputStream(in);
                    }
                    SocketMessage message = (SocketMessage) objectInputStream.readObject();

                    log.info("message received: {}", message);
                    notifyListeners(session, message);
                }
            } catch (Exception e) {
                log.error("Failed to handle connection: {}", e);
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void stop() {
        Thread.currentThread().interrupt();
    }
}