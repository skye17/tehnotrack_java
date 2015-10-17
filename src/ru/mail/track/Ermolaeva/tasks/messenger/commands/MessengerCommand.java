package ru.mail.track.Ermolaeva.tasks.messenger.commands;

import ru.mail.track.Ermolaeva.tasks.messenger.session.Session;

public abstract class MessengerCommand implements Command {
    public static final String PARAM_DELIMITER = "\\s+";
    protected String name;
    protected String description;
    protected Session session;

    public MessengerCommand(Session session) {
        this.session = session;
    }

    public MessengerCommand() {
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public abstract void execute(String argString);

    @Override
    public String getDescription() {
        return description;
    }

    protected void illegalArgument() {
        illegalArgument("Wrong number of arguments.");
    }

    protected void illegalArgument(String problemDescription) {
        throw new IllegalArgumentException("Wrong command usage." + problemDescription + "\nSee help:\n" + description);
    }

    protected String[] preprocessArgumentsString(String argsString) {
        if (argsString != null) {
            return argsString.trim().split(PARAM_DELIMITER);
        } else {
            return new String[]{};
        }
    }
}
