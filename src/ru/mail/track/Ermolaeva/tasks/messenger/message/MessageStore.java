package ru.mail.track.Ermolaeva.tasks.messenger.message;

import java.util.List;

/**
 * Хранилище информации о сообщениях
 */
public interface MessageStore {

    /**
     * получаем список ид пользователей заданного чата
     */
    List<Long> getChatsByUserId(Long userId);

    /**
     * получить информацию о чате
     */
    Chat getChatById(Long chatId);

    /**
     * Список сообщений из чата
     */
    List<Long> getMessagesFromChat(Long chatId);

    List<ChatMessage> getMessagesFromChatByRegex(Long chatId, String pattern);

    List<ChatMessage> getMessagesFromChatByRegex(Long chatId, String pattern, boolean caseFlag);

    /**
     * Получить информацию о сообщении
     */
    ChatMessage getMessageById(Long messageId);

    /**
     * Добавить сообщение в чат
     */
    void addMessage(Long messageId, Long chatId);

    /**
     * Добавить пользователя к чату
     */
    void addUserToChat(Long userId, Long chatId);

    void addMessage(ChatMessage message);

    void addChat(Chat chat);


}
