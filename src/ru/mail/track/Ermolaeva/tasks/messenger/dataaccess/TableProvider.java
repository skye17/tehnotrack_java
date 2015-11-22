package ru.mail.track.Ermolaeva.tasks.messenger.dataaccess;

import ru.mail.track.Ermolaeva.tasks.messenger.setters.Tables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TableProvider {
    private Map<TableType, AbstractDao> daoMap;
    private List<RelationshipDao> relationshipDaoList;
    private Map<TableType, Map<Integer, String>> tableColumns;
    private Map<TableType, String> tableNames;
    private List<Relation> relations;

    private QueryExecutor queryExecutor;

    public TableProvider(QueryExecutor queryExecutor) {
        this.queryExecutor = queryExecutor;
        this.tableNames = Tables.prepareTableNames();
        this.tableColumns = Tables.prepareTableColumnsNames();
        relations = Tables.prepareRelations();
        relationshipDaoList = new ArrayList<>();
        for (Relation relation : relations) {
            relationshipDaoList.add(new RelationshipDao(queryExecutor, relation));
        }
    }

    public void setUp() {
        daoMap = new HashMap<>();
        daoMap.put(TableType.CHAT, new ChatDao(queryExecutor, this, TableType.CHAT));
        daoMap.put(TableType.CHATMESSAGE, new ChatMessageDao(queryExecutor, this, TableType.CHATMESSAGE));
    }

    public AbstractUserDao getUserDao() {
        return new UserStore(queryExecutor, this, TableType.USER);
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
