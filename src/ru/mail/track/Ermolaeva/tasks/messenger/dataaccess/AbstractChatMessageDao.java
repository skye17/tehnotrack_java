package ru.mail.track.Ermolaeva.tasks.messenger.dataaccess;

import ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.exceptions.DataAccessException;
import ru.mail.track.Ermolaeva.tasks.messenger.message.ChatMessage;

import java.util.List;


public abstract class AbstractChatMessageDao extends AbstractDao<ChatMessage> {
    public AbstractChatMessageDao(QueryExecutor queryExecutor) {
        super(queryExecutor);
    }

    public abstract List<Long> getMessagesFromChat(Long chatId) throws DataAccessException;
}
