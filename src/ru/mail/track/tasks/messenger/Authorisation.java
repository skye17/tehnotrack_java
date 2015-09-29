package ru.mail.track.tasks.messenger;

import java.io.Console;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Simple class, records are not saved.
 */

public class Authorisation {
    protected Map<String, User> accounts;
    protected Scanner scanner;
    protected boolean hidePassword = false;

    public Authorisation() {
        accounts = new HashMap<>();
        scanner = new Scanner(System.in);
    }

    public Authorisation(boolean hidePassword) {
        this();
        this.hidePassword = hidePassword;
    }

    protected void run() {
        System.out.print("Hello! Let's sign in:\nUsername: ");
        if (scanner.hasNext()) {
            String username = scanner.next();
            if (accounts.containsKey(username)) {
                boolean rightPassword = checkUserPassword(username, readPassword());
                if (!rightPassword) {
                    System.out.println("Password is not correct.");
                } else {
                    System.out.println("You've successfully signed in.");
                }
            } else {
                System.out.println("Your username is not found. Do you want to create new account? y - 1/n - 0");
                if (scanner.hasNext("[0,1]")) {
                    int response = Integer.parseInt(scanner.next("[0,1]"));
                    if (response == 1) {
                        createNewUser(username);
                    }
                }
            }
        }
        System.out.println("Do you want to continue? y-1/n-0");
        if (scanner.hasNext("[0,1]")) {
            int response = Integer.parseInt(scanner.next("[0,1]"));
            if (response == 1) {
                run();
            } else {
                saveAndClose();
            }
        }
    }

    protected String readPassword() {
        String password = null;
        if (hidePassword) {
            scanner.close();
            Console console = System.console();
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

    protected void saveAndClose() {
        scanner.close();
    }

    protected void createNewUser(String username) {
        User user = new User(username, readPassword());
        accounts.put(username, user);
        System.out.println("You've successfully signed up.");
    }

    protected boolean checkUserPassword(String username, String password) {
        return accounts.get(username).getPassword().compareTo(password) == 0;
    }
}
