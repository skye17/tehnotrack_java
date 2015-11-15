package ru.mail.track.Ermolaeva.tasks.messenger.net;


// TODO: это кстати старый Jackson не рекомендуемый к использованию
import org.codehaus.jackson.map.ObjectMapper;
import ru.mail.track.Ermolaeva.tasks.messenger.InputHandler;
import ru.mail.track.Ermolaeva.tasks.messenger.commands.CommandType;
import ru.mail.track.Ermolaeva.tasks.messenger.commands.command_message.CommandMessage;
import ru.mail.track.Ermolaeva.tasks.messenger.session.Session;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;


public class ThreadedClient implements MessageListener {
    public static final int PORT = 19000;
    public static final String HOST = "localhost";
    private ConnectionHandler handler;

    public ThreadedClient() {
        try {
            Socket socket = new Socket(HOST, PORT);
            handler = new SocketConnectionHandler(socket);
            handler.addListener(this);

            Thread socketHandler = new Thread(handler);
            socketHandler.start();
        } catch (UnknownHostException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }
    }

    public static void main(String[] args) throws Exception {
        ThreadedClient client = new ThreadedClient();
        Scanner scanner = new Scanner(System.in);
        System.out.println("$");
        while (true) {
            if (scanner.hasNextLine()) {
                String input = scanner.nextLine();
                if ("/exit".equals(input)) {
                    client.handler.send(new CommandMessage(CommandType.LOGOUT));
                    System.exit(0);
                }

                try {
                    //FIXME: зачем на каждую итерацию создвать новый InputHandler? Это ошибка
                    client.handler.send(new InputHandler().processInput(input));
                } catch (IllegalArgumentException ex) {
                    System.out.println(ex.getMessage());
                }
            } else {
                break;
            }
        }
    }


    @Override
    public void update(Session session, SocketMessage message) {
        // FIXME: Это должно быть в Protocol, потому что клиенту не следует знать, в каком виде данные ходят по сети
        ResponseMessage responseMessage = (ResponseMessage) message;
        ObjectMapper mapper = new ObjectMapper();
        Object object;
        try {
            Class objectClass = Class.forName(responseMessage.getResultClass());
            object = mapper.readValue(responseMessage.getResponse(), objectClass);
            if (responseMessage.getStatus()) {
                System.out.println("Error!");
            }

            // FIXME: тем более вы смешали вывод данных с протоколом взаимодействия
            System.out.printf("%s\n", object);
            System.out.println("$");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Can't decode server response: " + e.getMessage());
        }

    }
}
