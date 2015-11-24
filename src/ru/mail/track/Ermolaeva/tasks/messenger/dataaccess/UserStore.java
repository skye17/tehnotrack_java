package ru.mail.track.Ermolaeva.tasks.messenger.dataaccess;

import ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.exceptions.DataAccessException;
import ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.exceptions.IllegalDataStateException;
import ru.mail.track.Ermolaeva.tasks.messenger.user.User;
import ru.mail.track.Ermolaeva.tasks.messenger.user.UserStatus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserStore extends AbstractUserDao {

    public UserStore(QueryExecutor queryExecutor, TableType tableType) {
        super(queryExecutor, tableType);
    }


    //@Override
    //public String getInsertQuery() {
    //    return "INSERT INTO " + tableName + " (login, password) VALUES (?, ?);";
    // }


    @Override
    protected Map<Integer, Object> prepareValuesForInsert(User object) throws DataAccessException {
        String login = object.getName();
        String password = object.getPassword();
        String nickname = object.getNickname();
        Map<Integer, Object> values = new HashMap<>();
        values.put(1, login);
        values.put(2, password);
        values.put(3, nickname);
        return values;
    }

    @Override
    protected Map<Integer, Object> prepareValuesForUpdate(User object) throws DataAccessException {
        if (object.getId() == null) {
            throw new DataAccessException("No such user");
        }
        String updateField = null;
        if (updateIndexes.size() == 1) {
            if (updateIndexes.get(0) == 3) {
                updateField = object.getPassword();
            } else {
                if (updateIndexes.get(0) == 4) {
                    updateField = object.getNickname();
                } else {
                    throw new DataAccessException("This update operation is not supported");
                }
            }
        }

        Map<Integer, Object> values = new HashMap<>();
        values.put(1, updateField);
        values.put(2, object.getId());
        return values;
    }

    @Override
    protected List<User> parseResultSet(ResultSet rs) {
        List<User> userList = new ArrayList<>();
        try {
            while (rs.next()) {
                Long id = rs.getLong(1);
                String login = rs.getString(2).trim();
                String password = rs.getString(3).trim();
                String nickname = rs.getString(4);
                if (nickname != null) {
                    nickname = nickname.trim();
                }
                userList.add(new User(id, login, password, nickname));
            }
            rs.close();
        } catch (SQLException e) {
            throw new IllegalDataStateException(e);
        }
        return userList;
    }

    @Override
    public UserStatus getUser(String username, String password) throws DataAccessException {
        if (username != null && password != null) {
            String sql = getSelectQuery() + " WHERE login = ?;";

            Map<Integer, Object> values = new HashMap<>();
            values.put(1, username);
            List<User> list = queryExecutor.executeQuery(sql, values, this::parseResultSet);
            if (list == null || list.size() == 0) {
                return new UserStatus(null, false);
            }

            if (list.size() > 1) {
                throw new IllegalDataStateException("Received more than one record.");
            }

            User user = list.get(0);
            if (user.getPassword().equals(password)) {
                // User существует и пароль верен
                return new UserStatus(user, true);
            } else {
                // User существует, но пароль не верен
                return new UserStatus(null, true);
            }

        }
        return new UserStatus(null, false);
    }
}
