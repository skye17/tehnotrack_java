package ru.mail.track.Ermolaeva.tasks.messenger.dataaccess;

import ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.exceptions.DataAccessException;
import ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.exceptions.IllegalDataStateException;
import ru.mail.track.Ermolaeva.tasks.messenger.message.ChatMessage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

public class ChatMessageDao extends AbstractChatMessageDao {
    private RelationshipDao relationshipDao;

    public ChatMessageDao(QueryExecutor queryExecutor) {
        super(queryExecutor);

        // FIXME: а это хардкод. Как минимум вынесите имена полей в константы
        setTableName("chatmessages");
        List<String> columnNamesList = new ArrayList<>();
        relationshipDao = new RelationshipDao(queryExecutor, tableName);
        columnNamesList.add("id");
        columnNamesList.add("chatid");
        relationshipDao.setColumnNames(new ArrayList<>(columnNamesList));

        columnNamesList.remove("chatid");
        columnNamesList.add("sender");
        columnNamesList.add("chatid");
        columnNamesList.add("pubdate");
        columnNamesList.add("messagetext");
        setColumnNames(columnNamesList);
    }

    @Override
    public String getInsertQuery() {
        return "INSERT INTO " + tableName + " (sender, chatid, pubdate, messageText) VALUES (?, ?, ?, ?);";
    }


    @Override
    protected Map<Integer, Object> prepareValuesForInsert(ChatMessage object) throws DataAccessException {
        Long sender = object.getSender();
        Long chat = object.getChatId();
        Timestamp timestamp = new Timestamp(object.getTimestamp().getTime());
        String messageText = object.getMessage();
        Map<Integer, Object> values = new HashMap<>();
        values.put(1, sender);
        values.put(2, chat);
        values.put(3, timestamp);
        values.put(4, messageText);
        return values;
    }

    @Override
    protected List<ChatMessage> parseResultSet(ResultSet rs) {
        List<ChatMessage> chatMessageList = new ArrayList<>();
        try {
            while (rs.next()) {
                Long id = rs.getLong(1);
                Long sender = rs.getLong(2);
                Long chat = rs.getLong(3);
                Date pubdate = new Date(rs.getTimestamp(4).getTime());
                String messageText = rs.getString(5);
                chatMessageList.add(new ChatMessage(id, sender, chat, pubdate, messageText));
            }
            rs.close();
        } catch (SQLException e) {
            throw new IllegalDataStateException(e);
        }
        return chatMessageList;
    }

    @Override
    public List<Long> getMessagesFromChat(Long chatId) throws DataAccessException {
        return relationshipDao.getBySecondKey(chatId);
    }

}
