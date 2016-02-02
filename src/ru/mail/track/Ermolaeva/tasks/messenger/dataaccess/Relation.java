package ru.mail.track.Ermolaeva.tasks.messenger.dataaccess;


import java.util.List;

public class Relation {
    private TableType firstKey;
    private TableType secondKey;
    private String relationTableName;
    private List<String> relationColumns;

    public Relation(TableType firstKey, TableType secondKey, String relationTableName, List<String> relationColumns) {
        this.firstKey = firstKey;
        this.secondKey = secondKey;
        this.relationTableName = relationTableName;
        this.relationColumns = relationColumns;
    }

    public TableType getFirstKey() {
        return firstKey;
    }

    public TableType getSecondKey() {
        return secondKey;
    }

    public String getRelationTableName() {
        return relationTableName;
    }

    public void setRelationTableName(String relationTableName) {
        this.relationTableName = relationTableName;
    }

    public List<String> getRelationColumns() {
        return relationColumns;
    }

    public void setRelationColumns(List<String> relationColumns) {
        this.relationColumns = relationColumns;
    }
}
