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
    static final String DELIMITER = "_";
    static final String FILE_EXTENSION = ".txt";

    private Map<String, MessageStore> userMessageStore;
    private Map<String, Boolean> isLoaded;
    private User currentUser;
    private String historyFile;

    public MessageService(String filename, Store userStore) throws IOException {
        if (filename != null) {
            historyFile = filename;
        }
        userMessageStore = new HashMap<>();
        isLoaded = new HashMap<>();

        for (User user : userStore.getUsers()) {
            userMessageStore.put(user.getName(), new MessageStoreImpl());
            isLoaded.put(user.getName(), false);
        }
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public void loadHistory(String userName) throws IOException {
        if (historyFile != null) {
            Path historyPath = Paths.get(historyFile + DELIMITER + userName + FILE_EXTENSION);
            if (Files.exists(historyPath)) {
                try (Scanner fileScanner = new Scanner(historyPath)) {
                    String username = null;
                    String timestamp;
                    String message;
                    String userString = fileScanner.findInLine(USERNAME_PATTERN);
                    if (userString != null) {
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
                    }
                }
            }
        }
        isLoaded.put(userName, true);
    }

    public boolean isLoaded(User user) {
        if (isLoaded.containsKey(user.getName())) {
            return isLoaded.get(user.getName());
        }
        return false;
    }

    public void close() throws IOException {
        for (String username : isLoaded.keySet()) {
            if (userMessageStore.get(username).size() != 0) {
                writeHistory(username);
            }
        }
    }

    private void writeHistory(String username) throws IOException {
        Path historyPath = Paths.get(historyFile + DELIMITER + username + FILE_EXTENSION);
        if (Files.notExists(historyPath)) {
            Files.createFile(historyPath);
        }

        try (FileWriter fileWriter = new FileWriter(historyPath.toFile())) {
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



    @Override
    public int size() {
        return userMessageStore.get(currentUser.getName()).size();
    }
}
