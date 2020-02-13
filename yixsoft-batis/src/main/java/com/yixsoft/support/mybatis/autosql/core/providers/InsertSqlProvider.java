package com.yixsoft.support.mybatis.autosql.core.providers;

import com.yixsoft.support.mybatis.autosql.core.IAutoSqlProvider;
import com.yixsoft.support.mybatis.autosql.dialects.ColumnInfo;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * sql builder for insert
 * Created by yixian on 2015-09-02.
 */
public class InsertSqlProvider extends AbstractSqlProvider implements IAutoSqlProvider {

    @Override
    protected String buildSql() {
        INSERT_INTO(getTableName());
        Map<String, Object> param = getParam();
        attachValues(param);
        return toString();
    }

    private void attachValues(Map<String, Object> param) {
        Set<String> usedKeySet = new HashSet<>();
        for (Map.Entry<String, Object> paramItem : param.entrySet()) {
            ColumnInfo info;
            String key = paramItem.getKey();
            if ((info = getTableColumnMap().get(key.toLowerCase())) != null && !usedKeySet.contains(key.toLowerCase())) {
                VALUES(getDialect().escapeKeyword(info.getColumn()), paramItem.getValue() == null ? "null" : "#{" + key + "}");
                //in case the object has multiple key references to same column(because key in map is case sensitive)
                usedKeySet.add(key.toLowerCase());
            }
        }
    }

}
