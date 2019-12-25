package com.yixsoft.support.mybatis.autosql.dialects;


import org.mybatis.spring.mapper.MapperFactoryBean;

import java.util.List;

/**
 * auto sql generator
 * Created by yixian on 2015-08-27.
 */
public interface ISqlDialect {

    void init(MapperFactoryBean parentFactory);

    /**
     * query column info for a table as this dialect
     *
     * @param tableName table name
     * @return column info list
     */
    List<ColumnInfo> selectTableColumns(String tableName);

    /**
     * escape keywords for this database in case some table name or column name uses the keywords in the database
     *
     * @param name table name or column name to be escaped
     * @return escaped name
     */
    String escapeKeyword(String name);
}
