package ru.mail.track.Ermolaeva.tasks.messenger.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static final int PORT = 19000;
    public static final String HOST = "localhost";

    private PrintStream out;
    private InputStream in;
    private Socket socket;

    public void run() {
        try {
            socket = new Socket(HOST, PORT);
            in = socket.getInputStream();
            out = new PrintStream(socket.getOutputStream(), true);

            byte[] buf = new byte[1024 * 64];
            Scanner scannerStdIn = new Scanner(System.in);
            while (true) {
                int readBytes = in.read(buf);
                if (readBytes >= 0) {
                    String line = new String(buf, 0, readBytes);
                    onMessage(line);
                } else {
                    break;
                }

                if (scannerStdIn.hasNextLine()) {
                    String userInput = scannerStdIn.nextLine();
                    send(userInput);
                } else {
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void send(String message) throws IOException {
        out.write(message.getBytes());
    }

    private void onMessage(String message) {
        System.out.println("Server: " + message);
    }

}
