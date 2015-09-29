package ru.mail.track.tasks.messenger;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Scanner;

public class FileAuthorisation extends Authorisation {
    private Path filePath;

    public FileAuthorisation(String filename) {
        this(filename, false);
        //loadAccounts(filename);
    }

    public FileAuthorisation(String filename, boolean hidePassword) {
        super(hidePassword);
        loadAccounts(filename);
    }

    private void loadAccounts(String filename) {
        filePath = Paths.get(filename);
        if (Files.exists(filePath)) {
            try (Scanner fileScanner = new Scanner(filePath)) {
                while (fileScanner.hasNext("(.)*:(.)*")) {
                    String keyValuePair = fileScanner.next("(.)*:(.)*");
                    String[] contents = keyValuePair.split(":");
                    if (contents.length == 2) {
                        User user = new User(contents[0], contents[1]);
                        accounts.put(contents[0], user);
                    }
                }
            } catch (IOException io) {

            }
        }
    }

    @Override
    protected void saveAndClose() {
        try {
            if (accounts.size() != 0 && Files.notExists(filePath)) {
                Files.createFile(filePath);
            }
            try (FileWriter fileWriter = new FileWriter(filePath.toFile())) {
                for (Map.Entry<String, User> entry : accounts.entrySet()) {
                    fileWriter.write(entry.getKey() + ":" + entry.getValue().getPassword() + "\n");
                }
            }
        } catch (IOException io) {

        }
        scanner.close();
    }
}
