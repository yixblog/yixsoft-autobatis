package com.yixsoft.support.mybatis.autosql.core.providers;

import com.yixsoft.support.mybatis.autosql.core.IAutoSqlProvider;

/**
 * sql builder for delete
 * Created by yixian on 2015-09-02.
 */
public class DeleteSqlProvider extends AbstractSqlProvider implements IAutoSqlProvider {

    @Override
    protected String buildSql() {
        DELETE_FROM(getTableName());
        buildWhereClause();
        return toString();
    }

}
