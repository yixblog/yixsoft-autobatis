package cn.yixblog.support.mybatis.autosql.core.providers;

import cn.yixblog.support.mybatis.autosql.core.IAutoSqlProvider;

import static org.apache.ibatis.jdbc.SqlBuilder.*;

/**
 * sql builder for delete
 * Created by yixian on 2015-09-02.
 */
public class DeleteSqlProvider extends AbstractSqlProvider implements IAutoSqlProvider {

    @Override
    public String getSql() {
        BEGIN();
        DELETE_FROM(getTableName());
        buildWhereClause();
        return SQL();
    }

}
