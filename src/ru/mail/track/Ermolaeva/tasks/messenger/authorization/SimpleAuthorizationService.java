package ru.mail.track.Ermolaeva.tasks.messenger.authorization;

import ru.mail.track.Ermolaeva.tasks.messenger.session.Store;
import ru.mail.track.Ermolaeva.tasks.messenger.session.User;

import java.io.Console;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class SimpleAuthorizationService implements AuthorizationService {
    protected Scanner scanner;
    protected Store userStore;
    protected Map<User, Boolean> isLogined;
    protected boolean hidePassword;
    //protected InputStream in;
    //protected PrintStream out;

    public SimpleAuthorizationService(Store userStore) {
        this(userStore, false);
    }


    public SimpleAuthorizationService(Store userStore, boolean hidePassword) {
        if (userStore != null) {
            this.userStore = userStore;
            this.hidePassword = hidePassword;
            isLogined = new HashMap<>();
            //scanner = new Scanner(in);
        }
    }


    @Override
    public User login(String username, String password) {
        if (username != null) {
            User result = userStore.getUser(username, password);
            //try {
            if (result == null) {
                //out.write("Information is incorrect. Login is unsuccessful".getBytes());
            } else {
                if (!isLogin(result)) {
                    //out.write("You've successfully signed in".getBytes());
                    isLogined.put(result, true);
                } else {
                    //out.write("Somebody already signed in your account!".getBytes());
                    return null;
                }
            }
            //} //catch (IOException io) {
            //throw new StreamException(io.getMessage());
            //}
            return result;
        }
        return null;
    }

    @Override
    public User createUser() {
        //try {
        //out.write("Username: ".getBytes());
        //scanner = new Scanner(in);
        String username = scanner.next();
        if (username != null) {
            // if (userStore.isUserExist(username)) {
            //   out.write("This username is already occupied.".getBytes());
        } else {
            String password = readPassword();
            User user = new User(username, password);
            userStore.addUser(user);
            //out.write("You've successfully signed up.".getBytes());
            return user;
        }
        //}
        //} //catch (IOException io) {
        //throw new StreamException(io.getMessage());
        //}
        return null;
    }


    @Override
    public boolean isLogin(User user) {
        if (isLogined.containsKey(user)) {
            return isLogined.get(user);
        }
        return false;
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
            //scanner = new Scanner(in);
        } else {
            //try {
            //out.write("Password: ".getBytes());
            //} catch (IOException e) {
            //  throw new StreamException(e.getMessage());
            //}
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
