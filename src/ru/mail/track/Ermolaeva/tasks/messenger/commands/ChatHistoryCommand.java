package ru.mail.track.Ermolaeva.tasks.messenger.commands;

import ru.mail.track.Ermolaeva.tasks.messenger.commands.command_message.ChatCommandMessage;
import ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.exceptions.DataAccessException;
import ru.mail.track.Ermolaeva.tasks.messenger.message.MessageStore;
import ru.mail.track.Ermolaeva.tasks.messenger.session.Session;

import java.util.ArrayList;
import java.util.List;


public class ChatHistoryCommand extends ChatCommand<ChatCommandMessage> {

    public ChatHistoryCommand(MessageStore messageStore) {
        super(messageStore);
        commandType = CommandType.CHAT_HISTORY;
        description = "/chat_history <chat_id> - get all messages from chat with <chat_id>";
    }

    @Override
    protected Result executeCommandChecked(Session session, ChatCommandMessage commandMessage) throws DataAccessException {
        Long chatId = commandMessage.getChatId();

        List<Long> list = messageStore.getMessagesFromChat(chatId);
        List<String> messagesList = new ArrayList<>();
        for (Long messageId : list) {
            messagesList.add(messageStore.getMessageById(messageId).toString());
        }
        return new CommandResult(messagesList);

    }

}
