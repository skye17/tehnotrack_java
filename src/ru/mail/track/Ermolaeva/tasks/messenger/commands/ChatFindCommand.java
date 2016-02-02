package ru.mail.track.Ermolaeva.tasks.messenger.commands;

import ru.mail.track.Ermolaeva.tasks.messenger.commands.command_message.ChatFindMessage;
import ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.exceptions.DataAccessException;
import ru.mail.track.Ermolaeva.tasks.messenger.message.ChatMessage;
import ru.mail.track.Ermolaeva.tasks.messenger.message.MessageStore;
import ru.mail.track.Ermolaeva.tasks.messenger.session.Session;

import java.util.List;

public class ChatFindCommand extends ChatCommand<ChatFindMessage> {
    public ChatFindCommand(MessageStore messageStore) {
        super(messageStore);
        commandType = CommandType.CHAT_FIND;
        description = "/chat_find <chat_id> <regex> - find all messages from chat <chat_id> containing <regex>";
        this.messageStore = messageStore;
    }

    @Override
    protected Result executeCommandChecked(Session session, ChatFindMessage commandMessage) throws DataAccessException {
        Long chatId = commandMessage.getChatId();
        String regex = commandMessage.getRegex();
        List<ChatMessage> list = messageStore.getMessagesFromChatByRegex(chatId, regex);
        return new CommandResult(list);
    }
}
