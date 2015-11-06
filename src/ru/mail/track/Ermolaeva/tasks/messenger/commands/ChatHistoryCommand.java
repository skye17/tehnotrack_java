package ru.mail.track.Ermolaeva.tasks.messenger.commands;

import ru.mail.track.Ermolaeva.tasks.messenger.command_message.ChatHistoryMessage;
import ru.mail.track.Ermolaeva.tasks.messenger.message.Chat;
import ru.mail.track.Ermolaeva.tasks.messenger.message.MessageStore;
import ru.mail.track.Ermolaeva.tasks.messenger.session.Session;

import java.util.ArrayList;
import java.util.List;


public class ChatHistoryCommand extends MessengerCommand<ChatHistoryMessage> {
    private MessageStore messageStore;

    public ChatHistoryCommand(MessageStore messageStore) {
        commandType = CommandType.CHAT_HISTORY;
        description = "/chat_history <chat_id> - get all messages from chat with <chat_id>";
        this.messageStore = messageStore;
    }

    @Override
    protected Result executeCommand(Session session, ChatHistoryMessage commandMessage) {
        Long chatId = commandMessage.getChatId();
        Chat chat = messageStore.getChatById(chatId);
        if (chat == null) {
            return new CommandResult("No chat with id " + chatId, true);
        } else {
            if (chat.getUsersList().contains(session.getCurrentUserId())) {
                List<Long> list = messageStore.getMessagesFromChat(chatId);
                List<String> messagesList = new ArrayList<>();
                for (Long messageId : list) {
                    messagesList.add(messageStore.getMessageById(messageId).toString());
                }
                return new CommandResult(messagesList);
            } else {
                return new CommandResult("You don't participate in the chat " + chatId, true);
            }
        }
    }

}
