package cn.yixblog.support.mybatis.autosql.core.providers;

import cn.yixblog.support.mybatis.autosql.core.IAutoSqlProvider;

import java.util.Map;

/**
 * Created by yixian on 2015-09-02.
 */
public class InsertSqlProvider extends AbstractSqlProvider implements IAutoSqlProvider {
    @Override
    public Map<String, Object> getAdditionalParams() {
        return null;
    }

    @Override
    public String getSql() {
        return null;
    }
}
