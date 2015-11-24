package ru.mail.track.Ermolaeva.tasks.messenger.dataaccess;

import ru.mail.track.Ermolaeva.tasks.messenger.setters.Tables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TableProvider {
    private volatile static TableProvider uniqueProvider;
    private Map<TableType, AbstractDao> daoMap;
    private List<RelationshipDao> relationshipDaoList;
    private Map<TableType, Map<Integer, String>> tableColumns;
    private Map<TableType, String> tableNames;
    private List<Relation> relations;
    private QueryExecutor queryExecutor;

    private TableProvider() {
        queryExecutor = new QueryExecutor();
        this.tableNames = Tables.prepareTableNames();
        this.tableColumns = Tables.prepareTableColumnsNames();
        relations = Tables.prepareRelations();
        relationshipDaoList = new ArrayList<>();
        for (Relation relation : relations) {
            relationshipDaoList.add(new RelationshipDao(queryExecutor, relation));
        }
    }

    public static TableProvider getInstance() {
        if (uniqueProvider == null) {
            synchronized (TableProvider.class) {
                if (uniqueProvider == null) {
                    uniqueProvider = new TableProvider();
                    uniqueProvider.setup();
                }
            }
        }
        return uniqueProvider;
    }

    private void setup() {
        daoMap = new HashMap<>();
        daoMap.put(TableType.CHAT, new ChatDao(queryExecutor, TableType.CHAT));
        daoMap.put(TableType.CHATMESSAGE, new ChatMessageDao(queryExecutor, TableType.CHATMESSAGE));
        daoMap.put(TableType.USER, new UserStore(queryExecutor, TableType.USER));
    }

    public AbstractUserDao getUserDao() {
        return (AbstractUserDao) daoMap.get(TableType.USER);
    }

    public <T extends Identified> AbstractDao<T> getDao(TableType tableType) {
        return daoMap.get(tableType);
    }


    public RelationshipDao getRelationDao(TableType firstKey, TableType secondKey) {
        for (RelationshipDao relationship : relationshipDaoList) {
            if (relationship.getFirstKeyType().equals(firstKey)
                    && relationship.getSecondKeyType().equals(secondKey)) {
                return relationship;
            }
        }
        return null;
    }

    public Map<Integer, String> getTableColumns(TableType tableType) {
        if (tableColumns.containsKey(tableType)) {
            return tableColumns.get(tableType);
        }
        return null;
    }

    public String getTableName(TableType tableType) {
        if (tableType != null && tableNames.containsKey(tableType)) {
            return tableNames.get(tableType);
        }
        return null;
    }


    public void setTableColumns(Map<TableType, Map<Integer, String>> tableColumns) {
        this.tableColumns = tableColumns;
    }

    public void setTableNames(Map<TableType, String> tableNames) {
        this.tableNames = tableNames;
    }

}
