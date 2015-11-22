package ru.mail.track.Ermolaeva.tasks.messenger.dataaccess;


import ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.exceptions.DataAccessException;
import ru.mail.track.Ermolaeva.tasks.messenger.user.User;
import ru.mail.track.Ermolaeva.tasks.messenger.user.UserStatus;

public abstract class AbstractUserDao extends AbstractDao<User> {
    public AbstractUserDao(QueryExecutor queryExecutor, TableProvider tableProvider, TableType tableType) {
        super(queryExecutor, tableProvider, tableType);
    }

    public abstract UserStatus getUser(String name, String password) throws DataAccessException;

}
