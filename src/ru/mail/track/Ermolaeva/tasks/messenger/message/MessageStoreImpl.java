package ru.mail.track.Ermolaeva.tasks.messenger.message;

import org.codehaus.jackson.map.ObjectMapper;
import ru.mail.track.Ermolaeva.tasks.messenger.net.ResponseMessage;
import ru.mail.track.Ermolaeva.tasks.messenger.session.SessionManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class MessageStoreImpl implements MessageStore {
    private Map<Long, Chat> chatStore;
    private Map<Long, ChatMessage> messageStore;
    private Map<Long, List<Long>> userChatsStore;
    private SessionManager sessionManager;


    public MessageStoreImpl() {
        chatStore = new HashMap<>();
        messageStore = new HashMap<>();
        userChatsStore = new HashMap<>();
    }

    public MessageStoreImpl(SessionManager sessionManager) {
        chatStore = new HashMap<>();
        messageStore = new HashMap<>();
        userChatsStore = new HashMap<>();
        this.sessionManager = sessionManager;
    }

    private void saveToDatabase() {

    }

    /**
     * получаем список чатов данного пользователя
     *
     * @param userId
     */
    @Override
    public List<Long> getChatsByUserId(Long userId) {
        if (userId != null) {
            if (!userChatsStore.containsKey(userId)) {
                userChatsStore.put(userId, new ArrayList<>());
            }
            return userChatsStore.get(userId);
        }
        return null;
    }

    /**
     * получить информацию о чате
     *
     * @param chatId
     */
    @Override
    public Chat getChatById(Long chatId) {
        if (chatId != null) {
            return chatStore.get(chatId);
        }
        return null;
    }

    /**
     * Список сообщений из чата
     *
     * @param chatId
     */
    @Override
    public List<Long> getMessagesFromChat(Long chatId) {
        Chat chat = getChatById(chatId);
        if (chat != null) {
            return chat.getMessageList();
        }
        return null;
    }

    @Override
    public List<ChatMessage> getMessagesFromChatByRegex(Long chatId, String regex) {
        return getMessagesFromChatByRegex(chatId, regex, true);
    }

    @Override
    public List<ChatMessage> getMessagesFromChatByRegex(Long chatId, String regex, boolean caseFlag) {
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

    /**
     * Получить информацию о сообщении
     *
     * @param messageId
     */
    @Override
    public ChatMessage getMessageById(Long messageId) {
        if (messageId != null) {
            return messageStore.get(messageId);
        }
        return null;
    }

    /**
     * Добавить сообщение в чат
     *
     * @param messageId
     * @param chatId
     */

    // TODO Возможно сделать его observable и рассылать observerам(участникам чата) уведомления о новых сообщениях
    @Override
    public void addMessage(Long messageId, Long chatId) {
        if (messageId != null && chatId != null) {
            Chat chat = getChatById(chatId);
            if (chat != null) {
                chat.addMessage(messageId);
                List<Long> usersList = new ArrayList<>(chat.getUsersList());

                ResponseMessage responseMessage = new ResponseMessage();
                ObjectMapper mapper = new ObjectMapper();
                String notification = "New message in chat " + chatId;
                try {
                    String jsonString = mapper.writeValueAsString(notification);
                    responseMessage.setResponse(jsonString);
                    responseMessage.setResultClass(String.class.getName());
                    usersList.remove(getMessageById(messageId).getSender());
                    for (Long userId : usersList) {
                        sessionManager.getSessionByUser(userId).getConnectionHandler().send(responseMessage);
                    }
                } catch (IOException e) {
                    //TODO
                }


            }
        }

    }

    /**
     * Добавить пользователя к чату
     *
     * @param userId
     * @param chatId
     */
    @Override
    public void addUserToChat(Long userId, Long chatId) {
        if (userId != null && chatId != null) {
            Chat chat = getChatById(chatId);
            if (chat != null) {
                chat.addUser(userId);
                if (!userChatsStore.containsKey(userId)) {
                    userChatsStore.put(userId, new ArrayList<>());
                }
                userChatsStore.get(userId).add(chatId);
            }
        }
    }

    @Override
    public void addMessage(ChatMessage message) {
        messageStore.put(message.getId(), message);
    }

    @Override
    public void addChat(Chat chat) {
        chatStore.put(chat.getId(), chat);
    }

}
