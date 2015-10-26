package ru.mail.track.Ermolaeva.tasks.messenger.message;

import ru.mail.track.Ermolaeva.tasks.messenger.session.Store;
import ru.mail.track.Ermolaeva.tasks.messenger.session.User;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

public class MessageService implements MessageStore {
    static final String USERNAME_PATTERN = "User:(.)*";
    static final String TIMESTAMP_PATTERN = "Timestamp:(.)*";
    static final String MESSAGE_START = "Message:";
    static final String MESSAGE_END = "Message;";
    static final String USER = "User:";
    static final String TIMESTAMP = "Timestamp:";
    private Map<String, MessageStore> userMessageStore;
    private User currentUser;
    private Path historyPath;
    private boolean isLoaded;

    public MessageService(String filename, Store userStore) throws IOException {
        if (filename != null) {
            historyPath = Paths.get(filename);
        }
        this.userMessageStore = new HashMap<>();
        for (User user : userStore.getUsers()) {
            userMessageStore.put(user.getName(), new MessageStoreImpl());
        }
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public void loadHistory() throws IOException {
        if (historyPath != null) {
            if (Files.exists(historyPath)) {
                try (Scanner fileScanner = new Scanner(historyPath)) {
                    String username = null;
                    String timestamp;
                    String message;
                    String userString = fileScanner.findInLine(USERNAME_PATTERN);
                    while (userString != null) {
                        String[] contents = userString.split(":");
                        if (contents.length == 2) {
                            username = contents[1];
                        }
                        if (username != null) {
                            if (userMessageStore.containsKey(username)) {
                                fileScanner.nextLine();
                                String timestampString = fileScanner.findInLine(TIMESTAMP_PATTERN);
                                while (timestampString != null) {
                                    timestamp = timestampString.substring(TIMESTAMP.length());
                                    fileScanner.nextLine();
                                    StringBuilder s = new StringBuilder();
                                    if (fileScanner.findInLine(MESSAGE_START) != null) {
                                        String line = fileScanner.next();
                                        Pattern delimiter = fileScanner.delimiter();
                                        fileScanner.useDelimiter("\n");
                                        while (!line.equals(MESSAGE_END)) {
                                            s.append(line);
                                            line = fileScanner.next();
                                        }
                                        fileScanner.useDelimiter(delimiter);
                                    }
                                    message = s.toString();

                                    userMessageStore.get(username).addMessage(new Message(message, timestamp));
                                    fileScanner.nextLine();
                                    timestampString = fileScanner.findInLine(TIMESTAMP_PATTERN);
                                }
                            }
                        }

                        userString = fileScanner.findInLine(USERNAME_PATTERN);
                    }
                }
            }
        }
        isLoaded = true;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public void close() throws IOException {
        writeHistory();
    }

    private void writeHistory() throws IOException {
        if (userMessageStore.size() != 0 && Files.notExists(historyPath)) {
            Files.createFile(historyPath);
        }

        try (FileWriter fileWriter = new FileWriter(historyPath.toFile())) {
            for (String username : userMessageStore.keySet()) {
                Collection<Message> messages = userMessageStore.get(username).getMessageHistory();
                if (messages.size() != 0) {
                    fileWriter.write(USER + username + "\n");
                    for (Message message : messages) {
                        fileWriter.write(TIMESTAMP + message.getTimestamp() + "\n");
                        fileWriter.write(MESSAGE_START + "\n" + message.getMessage() + "\n"
                                + MESSAGE_END + "\n");
                    }
                }
            }
        }

    }

    public void addMessage(String messageText) {
        addMessage(new Message(messageText, Calendar.getInstance().getTime()));
    }

    @Override
    public void addMessage(Message message) {
        MessageStore messageStore = userMessageStore.get(currentUser.getName());
        if (messageStore == null) {
            userMessageStore.put(currentUser.getName(), new MessageStoreImpl());

        }
        userMessageStore.get(currentUser.getName()).addMessage(message);

    }

    @Override
    public Collection<Message> getMessageHistory() {
        MessageStore messageStore = userMessageStore.get(currentUser.getName());
        if (messageStore != null) {
            return messageStore.getMessageHistory();
        }
        return null;
    }

    @Override
    public Collection<Message> getMessageHistory(int messagesNumber) {
        MessageStore messageStore = userMessageStore.get(currentUser.getName());
        if (messageStore != null) {
            return messageStore.getMessageHistory(messagesNumber);
        }
        return null;
    }

    @Override
    public Collection<Message> getMessagesByKeyword(String keyword) {
        MessageStore messageStore = userMessageStore.get(currentUser.getName());
        if (messageStore != null) {
            return messageStore.getMessagesByKeyword(keyword);
        }
        return null;
    }

    @Override
    public Collection<Message> getMessagesByPattern(String pattern, boolean caseFlag) {
        MessageStore messageStore = userMessageStore.get(currentUser.getName());
        if (messageStore != null) {
            return messageStore.getMessagesByPattern(pattern, caseFlag);
        }
        return null;
    }


    // bad part
    @Override
    public int size() {
        return 0;
    }

}
