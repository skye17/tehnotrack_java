package ru.mail.track.Ermolaeva.tasks.messenger.authorization;

import ru.mail.track.Ermolaeva.tasks.messenger.session.Store;
import ru.mail.track.Ermolaeva.tasks.messenger.session.User;

import java.io.Console;
import java.io.IOException;
import java.util.Scanner;

public class SimpleAuthorizationService implements AuthorizationService {
    protected Scanner scanner;
    protected Store userStore;
    protected boolean hidePassword;

    public SimpleAuthorizationService(Store userStore){
        if (userStore != null) {
            this.userStore = userStore;
            scanner = new Scanner(System.in);
        }
    }

    public SimpleAuthorizationService(Store userStore, boolean hidePassword) {
        this(userStore);
        this.hidePassword = hidePassword;
    }

    @Override
    public User login(String username, String password) {
        if (username != null) {
            User result = userStore.getUser(username, password);
            if (result == null) {
                System.out.println("Information is incorrect. Login is unsuccessful");
            } else {
                System.out.println("You've successfully signed in");
            }
            return result;
        }
        return null;
    }

    @Override
    public User createUser() {
        System.out.print("Username: ");
        scanner = new Scanner(System.in);
        String username = scanner.next();
        if (username != null) {
            if (userStore.isUserExist(username)) {
                System.out.println("This username is already occupied.");
            } else {
                String password = readPassword();
                User user = new User(username, password);
                userStore.addUser(user);
                System.out.println("You've successfully signed up.");
                return user;

            }
        }

        return null;
    }

    protected String readPassword() {
        String password = null;
        if (hidePassword) {
            scanner.close();
            Console console = System.console();
            System.out.println(console);
            if (console != null) {
                char[] passwordChar = console.readPassword("Password: ");
                if (passwordChar != null) {
                    password = new String(passwordChar);
                }
            }
            scanner = new Scanner(System.in);
        } else {
            System.out.print("Password: ");
            if (scanner.hasNext()) {
                password = scanner.next();
            }
        }
        return password;
    }

    @Override
    public void saveAndClose() throws IOException {
        scanner.close();
    }
}
