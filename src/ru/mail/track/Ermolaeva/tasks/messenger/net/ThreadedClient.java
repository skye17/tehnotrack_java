package ru.mail.track.Ermolaeva.tasks.messenger.net;

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
    private Thread socketHandler;

    public ThreadedClient() {
        try {
            Socket socket = new Socket(HOST, PORT);
            handler = new SocketConnectionHandler(socket);
            handler.addListener(this);
            socketHandler = new Thread(handler);
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
        InputHandler inputHandler = new InputHandler();
        System.out.println("$");
        while (true) {
            if (scanner.hasNextLine()) {
                String input = scanner.nextLine();
                if ("/exit".equals(input)) {
                    client.handler.send(new CommandMessage(CommandType.LOGOUT));
                    System.exit(0);
                }

                try {
                    client.handler.send(inputHandler.processInput(input));
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
        ResponseMessage responseMessage = (ResponseMessage) message;
        Object serverResponse = responseMessage.getResponseObject();
        if (serverResponse != null) {
            System.out.printf("%s\n", serverResponse);
            System.out.println("$");
        } else {
            System.out.println("Error: can't decode server response.");
        }
    }

    public void stop() {

        socketHandler.interrupt();
        try {
            socketHandler.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
