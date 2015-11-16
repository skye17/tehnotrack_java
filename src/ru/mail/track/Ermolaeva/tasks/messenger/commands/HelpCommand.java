package ru.mail.track.Ermolaeva.tasks.messenger.commands;


import ru.mail.track.Ermolaeva.tasks.messenger.commands.command_message.HelpMessage;
import ru.mail.track.Ermolaeva.tasks.messenger.session.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HelpCommand extends MessengerCommand<HelpMessage> {
    private Map<CommandType, Command> commands;

    public HelpCommand(Map<CommandType, Command> commands) {
        commandType = CommandType.HELP;
        description = "/help <command>- show information about <command>/all commands";
        needLogin = false;
        this.commands = commands;
        commands.put(commandType, this);
    }

    @Override
    protected Result executeCommand(Session session, HelpMessage commandMessage) {
        CommandType queryCommand = commandMessage.getCommand();
        List<String> result = new ArrayList<>();
        if (queryCommand != null) {
            result.add(commands.get(queryCommand).getType() + ":" + commands.get(queryCommand).getDescription() + "\n");
        } else {
            for (Command command : commands.values()) {
                result.add(command.getType() + ":" + command.getDescription() + "\n");
            }
        }
        return new CommandResult(result);
    }

}
