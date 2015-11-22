package ru.mail.track.Ermolaeva.tasks.messenger.dataaccess;


import ru.mail.track.Ermolaeva.tasks.messenger.dataaccess.exceptions.DataAccessException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RelationshipDao {
    private QueryExecutor queryExecutor;
    private String tableName;
    private Relation relation;
    private List<String> columnNames;

    public RelationshipDao(QueryExecutor queryExecutor, Relation relation) {
        this.queryExecutor = queryExecutor;
        this.relation = relation;
        this.tableName = relation.getRelationTableName();
        this.columnNames = relation.getRelationColumns();
    }

    public List<Long> getByFirstKey(Long firstKeyId) throws DataAccessException {
        String sql = "SELECT " + columnNames.get(1) + " FROM " + tableName + " WHERE " + columnNames.get(0) + "= ?;";
        Map<Integer, Object> values = new HashMap<>();
        values.put(1, firstKeyId);

        return queryExecutor.executeQuery(sql, values, (rs) -> {
            List<Long> result = new ArrayList<>();
            while (rs.next()) {
                result.add(rs.getLong(1));
            }
            return result;
        });
    }

    public List<Long> getBySecondKey(Long secondKeyId) throws DataAccessException {
        String sql = "SELECT " + columnNames.get(0) + " FROM " + tableName + " WHERE " + columnNames.get(1) + "= ?;";
        Map<Integer, Object> values = new HashMap<>();
        values.put(1, secondKeyId);

        return queryExecutor.executeQuery(sql, values, (rs) -> {
            List<Long> result = new ArrayList<>();
            while (rs.next()) {
                result.add(rs.getLong(1));
            }
            return result;
        });
    }

    public void addManyLinks(Map<Long, Long> keyIds) throws DataAccessException {
        String sql = "INSERT into " + tableName + " (" + columnNames.get(0) + "," + columnNames.get(1) + ") VALUES(?, ?);";
        List<Map<Integer, Object>> recordValues = new ArrayList<>();
        for (Map.Entry<Long, Long> entry : keyIds.entrySet()) {
            Map<Integer, Object> values = new HashMap<>();
            values.put(1, entry.getKey());
            values.put(2, entry.getValue());
            recordValues.add(values);
        }

        int[] updated = queryExecutor.executeUpdateBatch(sql, recordValues);
    }

    public void addLink(Long firstKeyId, Long secondKeyId) throws DataAccessException {
        String sql = "INSERT into " + tableName + " (" + columnNames.get(0) + "," + columnNames.get(1) + ") VALUES(?, ?);";
        Map<Integer, Object> values = new HashMap<>();
        values.put(1, firstKeyId);
        values.put(2, secondKeyId);

        int updated = queryExecutor.executeUpdate(sql, values);
        if (updated != 1) {
            throw new DataAccessException("On add deleted more than one record: " + updated);
        }
    }

    public void removeLink(Long firstKeyId, Long secondKeyId) throws DataAccessException {
        String sql = "DELETE from " + tableName + " WHERE " + columnNames.get(0) + "= ? AND " + columnNames.get(1) + " = ?;";
        Map<Integer, Object> values = new HashMap<>();
        values.put(1, firstKeyId);
        values.put(2, secondKeyId);

        int updated = queryExecutor.executeUpdate(sql, values);
        if (updated != 1) {
            throw new DataAccessException("On delete deleted more than one record: " + updated);
        }
    }

    public TableType getFirstKeyType() {
        return relation.getFirstKey();
    }

    public TableType getSecondKeyType() {
        return relation.getSecondKey();
    }
}
