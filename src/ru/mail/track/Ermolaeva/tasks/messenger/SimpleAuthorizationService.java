package ru.mail.track.Ermolaeva.tasks.messenger;

import java.io.Console;
import java.io.IOException;
import java.util.Scanner;

public class SimpleAuthorizationService implements AuthorizationService {
    protected Scanner scanner;
    protected Store userStore;
    protected boolean hidePassword = false;

    public SimpleAuthorizationService(Store userStore){
        Utility.isNull(userStore);
        this.userStore = userStore;
        scanner = new Scanner(System.in);
    }

    public SimpleAuthorizationService(Store userStore, boolean hidePassword) {
        this(userStore);
        this.hidePassword = hidePassword;
    }

    @Override
    public void startAuthorization() throws IOException {
        System.out.print("Let's sign in:\nUsername: ");
        if (scanner.hasNext()) {
            String username = scanner.next();
            if (userStore.isUserExist(username)) {
                if (login(username) == null) {
                    System.out.println("Password is not correct.");
                } else {
                    System.out.println("You've successfully signed in.");
                }
            } else {
                System.out.println("Your username is not found. Do you want to create new account? y - 1/n - 0");
                if (scanner.hasNextInt()) {
                    int response = scanner.nextInt();
                    if (response == 1) {
                        createUser(username);
                    }
                }
            }
        }

        System.out.println("Do you want to continue? y-1/n-0");
        if (scanner.hasNextInt()){
            int response = scanner.nextInt();
            if (response == 1) {
                startAuthorization();
            } else {
                saveAndClose();
            }
        }
    }

    @Override
    public User login(String username) {
        Utility.isNull(username);
        String password = readPassword();
        return userStore.getUser(username, password);
    }

    @Override
    public User createUser(String username) {
        Utility.isNull(username);
        String password = readPassword();
        User user = new User(username, password);
        userStore.addUser(user);
        System.out.println("You've successfully signed up.");
        return user;
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

    protected void saveAndClose() throws IOException {
        scanner.close();
    }



}
