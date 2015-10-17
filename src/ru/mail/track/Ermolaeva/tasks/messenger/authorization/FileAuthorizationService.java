package ru.mail.track.Ermolaeva.tasks.messenger.authorization;


import ru.mail.track.Ermolaeva.tasks.messenger.session.Store;
import ru.mail.track.Ermolaeva.tasks.messenger.session.User;

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
                        String tuple = fileScanner.next("(.)*:(.)*");
                        String[] contents = tuple.split(":");
                        if (contents.length == 2 || contents.length == 3) {
                            User user;
                            if (contents.length == 3) {
                                user = new User(contents[0], contents[1], contents[2]);
                            } else {
                                user = new User(contents[0], contents[1]);
                            }
                            userStore.addUser(user);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void saveAndClose() throws IOException {
        if (userStore.size() != 0 && Files.notExists(filePath)) {
            Files.createFile(filePath);
        }

        try (FileWriter fileWriter = new FileWriter(filePath.toFile())) {
            for (User user : userStore.getUsers()) {
                fileWriter.write(user.getName() + ":" + user.getPassword());
                if (user.getNickname() != null) {
                    fileWriter.write(":" + user.getNickname() + "\n");
                } else {
                    fileWriter.write("\n");
                }
            }
        }

        scanner.close();
    }
}
