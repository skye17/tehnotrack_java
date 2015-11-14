package ru.mail.track.Ermolaeva.tasks.messenger.dataaccess;

import java.util.ArrayList;
import java.util.List;


public class UserChatsDao extends RelationshipDao {

    public UserChatsDao(QueryExecutor queryExecutor) {
        super(queryExecutor, "userschats");
        List<String> columnNamesList = new ArrayList<>();
        columnNamesList.add("userid");
        columnNamesList.add("chatid");
        setColumnNames(columnNamesList);
    }

}
