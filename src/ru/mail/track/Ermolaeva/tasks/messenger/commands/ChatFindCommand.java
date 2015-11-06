package ru.mail.track.Ermolaeva.tasks.messenger.commands;

import ru.mail.track.Ermolaeva.tasks.messenger.command_message.ChatFindMessage;
import ru.mail.track.Ermolaeva.tasks.messenger.message.Chat;
import ru.mail.track.Ermolaeva.tasks.messenger.message.ChatMessage;
import ru.mail.track.Ermolaeva.tasks.messenger.message.MessageStore;
import ru.mail.track.Ermolaeva.tasks.messenger.session.Session;

import java.util.List;

public class ChatFindCommand extends MessengerCommand<ChatFindMessage> {
    private MessageStore messageStore;

    public ChatFindCommand(MessageStore messageStore) {
        commandType = CommandType.CHAT_FIND;
        description = "/chat_find <chat_id> <regex> - find all messages from chat <chat_id> containing <regex>";
        this.messageStore = messageStore;
    }

    @Override
    protected Result executeCommand(Session session, ChatFindMessage commandMessage) {
        Long chatId = commandMessage.getChatId();
        String regex = commandMessage.getRegex();
        Chat chat = messageStore.getChatById(chatId);
        if (chat == null) {
            return new CommandResult("No chat with id " + chatId, true);
        } else {
            if (chat.getUsersList().contains(session.getCurrentUserId())) {
                List<ChatMessage> list = messageStore.getMessagesFromChatByRegex(chatId, regex);
                return new CommandResult(list);
            } else {
                return new CommandResult("You don't participate in the chat " + chatId, true);
            }
        }


    }
}
