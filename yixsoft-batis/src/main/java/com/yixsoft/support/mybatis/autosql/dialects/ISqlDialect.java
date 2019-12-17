package com.yixsoft.support.mybatis.autosql.dialects;

import com.yixsoft.support.mybatis.autosql.configuration.support.dialect.SqlDialectManager;

import java.util.List;

/**
 * auto sql generator
 * Created by yixian on 2015-08-27.
 */
public abstract class ISqlDialect {

    public ISqlDialect() {
        SqlDialectManager.register(this);
    }

    /**
     * sql dialect for the database name
     * @return database name
     */
    public abstract String getName();

    /**
     * query column info for a table as this dialect
     * @param tableName table name
     * @return column info list
     */
    public abstract List<ColumnInfo> selectTableColumns(String tableName);

    /**
     * escape keywords for this database in case some table name or column name uses the keywords in the database
     * @param name    table name or column name to be escaped
     * @return escaped name
     */
    public abstract String escapeKeyword(String name);
}
