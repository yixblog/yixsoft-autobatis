package cn.yixblog.support.mybatis.autosql.core.providers;

import cn.yixblog.support.mybatis.autosql.core.IAutoSqlProvider;

import static org.apache.ibatis.jdbc.SqlBuilder.*;

/**
 * auto select sql builder
 * Created by yixian on 2015-09-02.
 */
public class SelectSqlProvider extends AbstractSqlProvider implements IAutoSqlProvider {

    @Override
    public String getSql() {
        BEGIN();
        SELECT("*");
        FROM(getTableName());
        buildWhereClause();
        return SQL();
    }
}
