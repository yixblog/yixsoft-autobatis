package cn.yixblog.support.mybatis.autosql.core.providers;

import cn.yixblog.support.mybatis.autosql.core.IAutoSqlProvider;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;

/**
 * create count sql
 * Created by yixian on 2015-09-02.
 */
public class CountSqlProvider extends AbstractSqlProvider implements IAutoSqlProvider {

    @Override
    public Map<String, Object> getAdditionalParams() {
        return null;
    }

    @Override
    public String getSql() {
        BEGIN();
        SELECT("count(*)");
        FROM(getTableName());

        WHERE("");
        return null;
    }

}
