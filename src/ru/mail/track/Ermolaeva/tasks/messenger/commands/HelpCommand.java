package ru.mail.track.Ermolaeva.tasks.messenger.commands;

import java.io.IOException;
import java.util.ArrayList;
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
    public CommandResult execute(String argsString) {
        String[] arguments = preprocessArgumentsString(argsString);
        final ArrayList<String> result;
        if (arguments.length == 0) {
            result = new ArrayList<>();
            for (String commandName : commands.keySet()) {
                result.add(commandName + ":\n" + commands.get(commandName));
            }

        } else {
            result = new ArrayList<>();
            if (arguments.length == 1) {
                if (commands.containsKey(arguments[0])) {
                    result.add(arguments[0] + ":\n" + commands.get(arguments[0]));
                } else {
                    result.add("Command not found");
                }
            } else {
                illegalArgument();
            }
        }

        return out -> result.stream().filter(string -> string != null).forEach(s -> {
            try {
                out.write(s.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
