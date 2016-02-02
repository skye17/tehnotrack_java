package ru.mail.track.Ermolaeva.tasks.messenger.dataaccess;

import ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.exceptions.DataAccessException;
import ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.exceptions.IllegalDataStateException;
import ru.mail.track.Ermolaeva.tasks.messenger.message.Chat;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatDao extends AbstractDao<Chat> {

    public ChatDao(QueryExecutor queryExecutor, TableType tableType) {
        super(queryExecutor, tableType);
    }


    @Override
    protected Map<Integer, Object> prepareValuesForUpdate(Chat object) throws DataAccessException {
        if (object.getId() == null) {
            throw new DataAccessException("No such chat");
        }
        String updateField;
        if (updateIndexes.size() == 1 && updateIndexes.get(0) == 2) {
            updateField = object.getTitle();
        } else {
            throw new DataAccessException("This update operation is not supported");
        }
        Map<Integer, Object> values = new HashMap<>();
        values.put(1, updateField);
        values.put(2, object.getId());
        return values;
    }

    @Override
    public String getInsertQuery() {
        return "INSERT INTO " + tableName + " DEFAULT VALUES;";
    }

    @Override
    protected Map<Integer, Object> prepareValuesForInsert(Chat object) throws DataAccessException {
        return new HashMap<>();
    }

    @Override
    protected List<Chat> parseResultSet(ResultSet rs) {
        List<Chat> chatsList = new ArrayList<>();
        try {
            while (rs.next()) {
                Long id = rs.getLong(1);
                Chat newChat = new Chat();
                newChat.setId(id);
                chatsList.add(newChat);
            }
            rs.close();
        } catch (SQLException e) {
            throw new IllegalDataStateException(e);
        }
        return chatsList;
    }
}
