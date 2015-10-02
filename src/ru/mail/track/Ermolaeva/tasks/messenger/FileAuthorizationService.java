package ru.mail.track.Ermolaeva.tasks.messenger;


import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class FileAuthorizationService extends SimpleAuthorizationService {
    private Path filePath;

    public FileAuthorizationService(Store userStore, String filename) throws IOException {
        this(userStore, filename, false);
    }

    public FileAuthorizationService(Store userStore, String filename, boolean hidePassword) throws IOException {
        super(userStore, hidePassword);
        loadAccounts(filename);
    }

    private void loadAccounts(String filename) throws IOException {
        if (filename != null) {
            filePath = Paths.get(filename);
            if (Files.exists(filePath)) {
                try (Scanner fileScanner = new Scanner(filePath)) {
                    while (fileScanner.hasNext("(.)*:(.)*")) {
                        String keyValuePair = fileScanner.next("(.)*:(.)*");
                        String[] contents = keyValuePair.split(":");
                        if (contents.length == 2) {
                            User user = new User(contents[0], contents[1]);
                            userStore.addUser(user);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void saveAndClose() throws IOException {
        if (userStore.size() != 0 && Files.notExists(filePath)) {
            Files.createFile(filePath);
        }

        try (FileWriter fileWriter = new FileWriter(filePath.toFile())) {
            for (User user : userStore.getUsers()) {
                fileWriter.write(user.getName() + ":" + user.getPassword() + "\n");
            }
        }

        scanner.close();
    }
}
