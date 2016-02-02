package ru.mail.track.Ermolaeva.tasks.messenger.commands;

import ru.mail.track.Ermolaeva.tasks.messenger.commands.command_message.ChatSendMessage;
import ru.mail.track.Ermolaeva.tasks.messenger.commands.command_message.CommandMessage;
import ru.mail.track.Ermolaeva.tasks.messenger.commands.exceptions.IllegalCommandException;
import ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.exceptions.DataAccessException;
import ru.mail.track.Ermolaeva.tasks.messenger.message.ChatMessage;
import ru.mail.track.Ermolaeva.tasks.messenger.message.MessageStore;
import ru.mail.track.Ermolaeva.tasks.messenger.session.Session;

import java.util.function.Function;


public class ChatSendCommand extends ChatCommand<ChatSendMessage> {
    public static final String MESSAGE_ADDED = "Message successfully added to chat ";

    public ChatSendCommand(MessageStore messageStore) {
        super(messageStore);
        commandType = CommandType.CHAT_SEND;
        description = "/chat_send <id> <message> - send <message> to chat with <id>";
    }

    @Override
    protected Result executeCommandChecked(Session session, ChatSendMessage commandMessage) throws DataAccessException {
        Long chatId = commandMessage.getChatId();
        String messageText = commandMessage.getMessageToChat();

        ChatMessage message = new ChatMessage(messageText);
        message.setSender(session.getCurrentUserId());
        message.setChatId(chatId);
        Long messageId = messageStore.addMessageToStore(message);
        messageStore.addMessage(messageId, chatId);
        return new CommandResult(MESSAGE_ADDED + chatId);
    }

    @Override
    public Function<String, ? extends CommandMessage> getArgumentParser() {
        return argInput -> {
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
        };
    }
}
