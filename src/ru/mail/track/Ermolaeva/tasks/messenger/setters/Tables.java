package ru.mail.track.Ermolaeva.tasks.messenger.setters;


import ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.Relation;
import ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.TableType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tables {
    public static final String USERTABLE = "users";
    public static final String CHATTABLE = "chats";
    public static final String MESSAGETABLE = "chatmessages";
    public static final String USERID = "userid";
    public static final String CHATID = "chatid";
    public static final String USERCHATSTABLE = "userschats";
    public static final String ID = "id";

    public static Map<TableType, String> prepareTableNames() {
        Map<TableType, String> tableNames = new HashMap<>();
        tableNames.put(TableType.CHAT, CHATTABLE);
        tableNames.put(TableType.USER, USERTABLE);
        tableNames.put(TableType.CHATMESSAGE, MESSAGETABLE);
        return tableNames;
    }

    public static List<Relation> prepareRelations() {
        List<Relation> relations = new ArrayList<>();
        List<String> columnList = new ArrayList<>();
        columnList.add(USERID);
        columnList.add(CHATID);
        relations.add(new Relation(TableType.USER, TableType.CHAT, USERCHATSTABLE, new ArrayList<>(columnList)));
        columnList = null;
        columnList = new ArrayList<>();
        columnList.add(ID);
        columnList.add(CHATID);
        relations.add(new Relation(TableType.CHATMESSAGE, TableType.CHAT, MESSAGETABLE, columnList));
        return relations;
    }


    public static Map<TableType, Map<Integer, String>> prepareTableColumnsNames() {
        Map<TableType, Map<Integer, String>> tableColumnsNames = new HashMap<>();
        Map<Integer, String> columnNames = new HashMap<>();
        columnNames.put(2, "login");
        columnNames.put(3, "password");
        columnNames.put(4, "nickname");
        tableColumnsNames.put(TableType.USER, new HashMap<>(columnNames));
        columnNames = null;
        columnNames = new HashMap<>();
        columnNames.put(2, "chat_title");
        tableColumnsNames.put(TableType.CHAT, new HashMap<>(columnNames));
        columnNames = null;
        columnNames = new HashMap<>();
        columnNames.put(2, "sender");
        columnNames.put(3, "chatid");
        columnNames.put(4, "pubdate");
        columnNames.put(5, "messagetext");
        tableColumnsNames.put(TableType.CHATMESSAGE, new HashMap<>(columnNames));
        columnNames = null;
        return tableColumnsNames;
    }

}
