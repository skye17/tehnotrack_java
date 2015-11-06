package ru.mail.track.Ermolaeva.tasks.messenger;


import ru.mail.track.Ermolaeva.tasks.messenger.command_message.*;
import ru.mail.track.Ermolaeva.tasks.messenger.commands.CommandType;
import ru.mail.track.Ermolaeva.tasks.messenger.exceptions.IllegalCommandException;
import ru.mail.track.Ermolaeva.tasks.messenger.net.CommandMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;


public class ParserFactory {
    public static final String PARAM_DELIMITER = "\\s+";
    Map<CommandType, Function<String, CommandMessage>> parsersMap = new HashMap<>();

    public ParserFactory() {
        parsersMap = new HashMap<>();
        setDefault();

    }

    public Function<String, CommandMessage> getParser(CommandType commandType) {
        if (parsersMap.containsKey(commandType)) {
            return parsersMap.get(commandType);
        } else {
            throw new IllegalCommandException("No such command");
        }
    }


    private String[] checkArgumentsNumber(String input, int argumentsNumber) {
        String[] tokens = input.split(PARAM_DELIMITER);
        if (tokens.length != argumentsNumber) {
            throw new IllegalCommandException("Wrong number of arguments: expected " + argumentsNumber
                    + ". Type /help <command> for more information");
        } else {
            return tokens;
        }
    }

    public void setDefault() {
        parsersMap.put(CommandType.LOGIN, argInput -> {
            String[] tokens = checkArgumentsNumber(argInput, 2);
            LoginMessage commandMessage = new LoginMessage();
            commandMessage.setLogin(tokens[0]);
            commandMessage.setPass(tokens[1]);
            return commandMessage;
        });

        parsersMap.put(CommandType.HELP, argInput -> {
            HelpMessage commandMessage = new HelpMessage();
            if (argInput.equals("")) {
                return commandMessage;
            } else {
                try {
                    CommandType commandType = CommandType.valueOf(argInput.toUpperCase());
                    commandMessage.setCommand(commandType);
                    return commandMessage;
                } catch (IllegalArgumentException ex) {
                    throw new IllegalCommandException("Illegal command: " + argInput);
                }
            }
        });

        parsersMap.put(CommandType.USER, argInput -> {
            String[] tokens = checkArgumentsNumber(argInput, 1);
            UserMessage commandMessage = new UserMessage();
            commandMessage.setNickname(tokens[0]);
            return commandMessage;
        });

        parsersMap.put(CommandType.USER_INFO, argInput -> {
            UserInfoMessage commandMessage = new UserInfoMessage();
            if (argInput.equals("")) {
                return commandMessage;
            } else {
                try {
                    Long value = Long.valueOf(argInput);
                    commandMessage.setUserId(value);
                    return commandMessage;
                } catch (NumberFormatException ex) {
                    throw new IllegalCommandException("User id should be a number");
                }
            }
        });

        parsersMap.put(CommandType.USER_PASS, argInput -> {
            String[] tokens = checkArgumentsNumber(argInput, 2);
            UserPassMessage commandMessage = new UserPassMessage();
            commandMessage.setOldPassword(tokens[0]);
            commandMessage.setNewPassword(tokens[1]);
            return commandMessage;
        });

        parsersMap.put(CommandType.CHAT_CREATE, argInput -> {
            String[] tokens = checkArgumentsNumber(argInput, 1);
            ChatCreateMessage chatCreateMessage = new ChatCreateMessage();
            String idList = tokens[0];
            String[] ids = idList.split(",");
            List<Long> usersIdList = new ArrayList<>();
            for (String id : ids) {
                try {
                    Long value = Long.valueOf(id);
                    usersIdList.add(value);
                } catch (IllegalArgumentException io) {
                    throw new IllegalCommandException("User id should be a number");
                }
            }
            chatCreateMessage.setUserIdList(usersIdList);
            return chatCreateMessage;
        });

        parsersMap.put(CommandType.CHAT_LIST, argInput -> {
            if (argInput.equals("")) {
                return new ChatListMessage();
            } else {
                throw new IllegalCommandException("Wrong number of arguments. " +
                        "Type /help chat_list for more information.");
            }
        });

        parsersMap.put(CommandType.LOGOUT, argInput -> {
            checkArgumentsNumber(argInput, 1);
            return new LogoutMessage();
        });

        parsersMap.put(CommandType.CHAT_HISTORY, argInput -> {
            String[] tokens = checkArgumentsNumber(argInput, 1);
            try {
                Long value = Long.valueOf(tokens[0]);
                ChatHistoryMessage chatHistoryMessage = new ChatHistoryMessage();
                chatHistoryMessage.setChatId(value);
                return chatHistoryMessage;
            } catch (NumberFormatException ex) {
                throw new IllegalCommandException("Chat id should be a number");
            }
        });

        parsersMap.put(CommandType.CHAT_FIND, argInput -> {
            String argument = argInput.trim();
            int whitespaceIndex = argument.indexOf(" ");
            if (whitespaceIndex < 0) {
                throw new IllegalCommandException("Type /help chat_find for more information");
            } else {
                String chatId = argument.substring(0, whitespaceIndex);
                try {
                    Long value = Long.valueOf(chatId);
                    argument = argument.substring(whitespaceIndex + 1).trim();
                    ChatFindMessage chatFindMessage = new ChatFindMessage();
                    chatFindMessage.setChatId(value);
                    chatFindMessage.setRegex(argument);
                    return chatFindMessage;
                } catch (NumberFormatException ex) {
                    throw new IllegalCommandException("Chat id should be a number");
                }

            }
        });

        parsersMap.put(CommandType.CHAT_SEND, argInput -> {
            String argument = argInput.trim();
            int whitespaceIndex = argument.indexOf(" ");
            if (whitespaceIndex < 0) {
                throw new IllegalCommandException("Type /help chat_send for more information");
            } else {
                String chatIdString = argument.substring(0, whitespaceIndex);
                try {
                    Long chatId = Long.valueOf(chatIdString);
                    argument = argument.substring(whitespaceIndex + 1).trim();
                    ChatSendMessage commandMessage = new ChatSendMessage();
                    commandMessage.setChatId(chatId);
                    commandMessage.setMessageToChat(argument);
                    return commandMessage;
                } catch (NumberFormatException ex) {
                    throw new IllegalCommandException("Chat id should be a number");
                }

            }

        });
    }
}