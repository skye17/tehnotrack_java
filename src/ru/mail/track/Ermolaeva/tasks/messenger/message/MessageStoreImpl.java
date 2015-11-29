package ru.mail.track.Ermolaeva.tasks.messenger.message;

import ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.*;
import ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.exceptions.DataAccessException;
import ru.mail.track.Ermolaeva.tasks.messenger.net.ResponseMessage;
import ru.mail.track.Ermolaeva.tasks.messenger.session.Session;
import ru.mail.track.Ermolaeva.tasks.messenger.session.SessionManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class MessageStoreImpl implements MessageStore {
    private GenericDao<ChatMessage> chatMessageDao;
    private AbstractDao<Chat> chatDao;
    private RelationshipDao userChatsDao;
    private RelationshipDao chatMessagesDao;

    private SessionManager sessionManager;

    public MessageStoreImpl(TableProvider tableProvider, SessionManager sessionManager) {
        if (tableProvider != null && sessionManager != null) {
            this.sessionManager = sessionManager;
            chatMessageDao = tableProvider.getDao(TableType.CHATMESSAGE);
            chatDao = tableProvider.getDao(TableType.CHAT);
            userChatsDao = tableProvider.getRelationDao(TableType.USER, TableType.CHAT);
            chatMessagesDao = tableProvider.getRelationDao(TableType.CHATMESSAGE, TableType.CHAT);
        }
    }


    private void updateUsers(List<Long> usersList, String notification) throws DataAccessException {
        try {
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setResponseObject(notification);
            for (Long userId : usersList) {
                Session session = sessionManager.getSessionByUser(userId);
                if (session != null) {
                    sessionManager.getSessionByUser(userId).getConnectionHandler().send(responseMessage);
                }
            }
        } catch (IOException e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public List<Long> getChatsByUserId(Long userId) throws DataAccessException {
        if (userId != null) {
            return userChatsDao.getByFirstKey(userId);
        }
        return null;
    }


    @Override
    public Chat getChatById(Long chatId) throws DataAccessException {
        if (chatId != null) {
            Chat chat = chatDao.getById(chatId);
            if (chat != null) {
                List<Long> messages = chatMessagesDao.getBySecondKey(chatId);
                List<Long> users = userChatsDao.getBySecondKey(chatId);
                chat.setUsersList(users);
                chat.setMessageList(messages);
            }
            return chat;
        }
        return null;
    }

    @Override
    public void updateChat(Chat chat) throws DataAccessException {
        if (chat != null) {
            chatDao.setUpdateIndexes(2);
            chatDao.update(chat);
            String notification = "Title changed in chat " + chat.getId();
            updateUsers(chat.getUsersList(), notification);
        }
    }

    @Override
    public List<Long> getMessagesFromChat(Long chatId) throws DataAccessException {
        if (chatId != null) {
            Chat chat = getChatById(chatId);
            return chat.getMessageList();
        }
        return null;
    }

    @Override
    public List<ChatMessage> getMessagesFromChatByRegex(Long chatId, String regex) throws DataAccessException {
        return getMessagesFromChatByRegex(chatId, regex, true);
    }

    @Override
    public List<ChatMessage> getMessagesFromChatByRegex(Long chatId, String regex, boolean caseFlag) throws DataAccessException {
        ArrayList<ChatMessage> result = new ArrayList<>();
        List<Long> messagesIdList = getMessagesFromChat(chatId);
        if (messagesIdList != null) {
            for (Long messageId : messagesIdList) {
                ChatMessage message = getMessageById(messageId);
                if (Pattern.compile(regex).matcher(message.getMessage()).find(Pattern.CASE_INSENSITIVE)) {
                    result.add(message);
                } else {
                    if (Pattern.compile(regex).matcher(message.getMessage()).find()) {
                        result.add(message);
                    }
                }
            }
        }
        return result;
    }


    @Override
    public ChatMessage getMessageById(Long messageId) throws DataAccessException {
        if (messageId != null) {
            return chatMessageDao.getById(messageId);
        }
        return null;
    }

    @Override
    public void addMessage(Long messageId, Long chatId) throws DataAccessException {
        if (messageId != null && chatId != null) {
            Chat chat = getChatById(chatId);
            if (chat != null) {
                List<Long> usersList = new ArrayList<>(chat.getUsersList());
                String message = getMessageById(messageId).getMessage();
                String notification = "New message in chat " + chatId + ":" + message;
                usersList.remove(getMessageById(messageId).getSender());
                updateUsers(usersList, notification);
            }
        }

    }

    @Override
    public Chat addUserToChat(Long userId, Long chatId) throws DataAccessException {
        if (userId != null && chatId != null) {
            userChatsDao.addLink(userId, chatId);
            return getChatById(chatId);
        }
        return null;
    }

    @Override
    public void addUsersToChat(List<Long> userIds, Long chatId) throws DataAccessException {
        Map<Long, Long> ids = new HashMap<>();
        for (Long userId : userIds) {
            ids.put(userId, chatId);
        }
        userChatsDao.addManyLinks(ids);
    }

    @Override
    public void removeUserFromChat(Long userId, Long chatId) throws DataAccessException {
        if (userId != null && chatId != null) {
            userChatsDao.removeLink(userId, chatId);
        }
    }

    @Override
    public Long addMessageToStore(ChatMessage message) throws DataAccessException {
        message = chatMessageDao.add(message);
        return message.getId();
    }

    @Override
    public Long addChatToStore(Chat chat) throws DataAccessException {
        chat = chatDao.add(chat);
        return chat.getId();

    }

}
