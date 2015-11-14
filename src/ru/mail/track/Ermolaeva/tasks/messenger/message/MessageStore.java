package ru.mail.track.Ermolaeva.tasks.messenger.message;


import ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.GenericDaoUpdatable;
import ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.exceptions.DataAccessException;

import java.util.List;

/**
 * Хранилище информации о сообщениях
 */
public interface MessageStore {

    /**
     * получаем список ид пользователей заданного чата
     */
    List<Long> getChatsByUserId(Long userId) throws DataAccessException;

    /**
     * получить информацию о чате
     */
    Chat getChatById(Long chatId) throws DataAccessException;


    GenericDaoUpdatable<Chat> getChatDao();

    /**
     * Список сообщений из чата
     */
    List<Long> getMessagesFromChat(Long chatId) throws DataAccessException;

    List<ChatMessage> getMessagesFromChatByRegex(Long chatId, String pattern) throws DataAccessException;

    List<ChatMessage> getMessagesFromChatByRegex(Long chatId, String pattern, boolean caseFlag) throws DataAccessException;

    /**
     * Получить информацию о сообщении
     */
    ChatMessage getMessageById(Long messageId) throws DataAccessException;


    /**
     * Добавить сообщение в чат
     */
    void addMessage(Long messageId, Long chatId) throws DataAccessException;

    /**
     * Добавить пользователя к чату
     */
    Chat addUserToChat(Long userId, Long chatId) throws DataAccessException;

    void removeUserFromChat(Long userId, Long chatId) throws DataAccessException;

    void addUsersToChat(List<Long> userIds, Long chatId) throws DataAccessException;

    Long addMessageToStore(ChatMessage message) throws DataAccessException;

    Long addChatToStore(Chat chat) throws DataAccessException;
}
