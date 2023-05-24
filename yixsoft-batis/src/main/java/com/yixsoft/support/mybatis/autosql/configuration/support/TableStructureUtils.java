package com.yixsoft.support.mybatis.autosql.configuration.support;

import com.yixsoft.support.mybatis.autosql.dialects.ColumnInfo;
import com.yixsoft.support.mybatis.autosql.dialects.ISqlDialect;
import com.yixsoft.support.mybatis.exceptions.MissingTableException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableStructureUtils {
    private TableStructureUtils(){}
    private static final Map<String, Map<String, ColumnInfo>> tableStructureCache = new HashMap<>();

    public static Map<String, ColumnInfo> getTableColumns(ISqlDialect dialect, String tableName) {
        if (tableStructureCache.containsKey(tableName)) {
            return new HashMap<>(tableStructureCache.get(tableName));
        }
        synchronized (tableStructureCache) {
            if (tableStructureCache.containsKey(tableName)) {
                return new HashMap<>(tableStructureCache.get(tableName));
            }
            Map<String, ColumnInfo> structure = loadTableColumns(dialect, tableName);
            tableStructureCache.put(tableName, structure);
            return new HashMap<>(structure);
        }
    }

    private static Map<String, ColumnInfo> loadTableColumns(ISqlDialect dialect, String tableName) {
        List<ColumnInfo> columns = dialect.selectTableColumns(tableName);
        if (columns.isEmpty()) {
            throw new MissingTableException("Table " + tableName + " not found in database");
        }
        Map<String, ColumnInfo> columnMap = new HashMap<>();
        for (ColumnInfo col : columns) {
            columnMap.put(col.getColumn().toLowerCase(), col);
        }
        return columnMap;
    }
}
