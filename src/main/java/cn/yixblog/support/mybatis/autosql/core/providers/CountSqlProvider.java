package cn.yixblog.support.mybatis.autosql.core.providers;

import cn.yixblog.support.mybatis.autosql.core.IAutoSqlProvider;

import static org.apache.ibatis.jdbc.SqlBuilder.*;

/**
 * create count sql
 * Created by yixian on 2015-09-02.
 */
public class CountSqlProvider extends AbstractSqlProvider implements IAutoSqlProvider {

    @Override
    public String getSql() {
        BEGIN();
        SELECT("count(*) total");
        FROM(getTableName());
        buildWhereClause();
        return SQL();
    }

}