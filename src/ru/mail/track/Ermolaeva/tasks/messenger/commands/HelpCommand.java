package ru.mail.track.Ermolaeva.tasks.messenger.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HelpCommand extends MessengerCommand {

    private Map<String, String> commands;

    public HelpCommand(List<Command> commands) {
        name = "help";
        description = "/help - show information about all commands\n/help <command> - show information about <command>";
        this.commands = new HashMap<>();
        this.commands.put(name, description);
        for (Command command : commands) {
            this.commands.put(command.getName(), command.getDescription());
        }

    }

    @Override
    public void execute(String argsString) {
        String[] arguments = preprocessArgumentsString(argsString);
        if (arguments.length == 0) {
            for (String commandName : commands.keySet()) {
                System.out.println(commandName + ":\n" + commands.get(commandName));
            }
        } else {
            if (arguments.length == 1) {
                if (commands.containsKey(arguments[0])) {
                    System.out.println(arguments[0] + ":\n" + commands.get(arguments[0]));
                } else {
                    System.out.println("Command not found");
                }
            } else {
                illegalArgument();
            }
        }
    }
}
