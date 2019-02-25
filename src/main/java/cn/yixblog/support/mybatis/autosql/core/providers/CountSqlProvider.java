package cn.yixblog.support.mybatis.autosql.core.providers;

import cn.yixblog.support.mybatis.autosql.core.IAutoSqlProvider;
import org.apache.commons.lang3.StringUtils;


/**
 * create count sql
 * Created by yixian on 2015-09-02.
 */
public class CountSqlProvider extends AbstractSqlProvider implements IAutoSqlProvider {

    private String addonWhereClause;

    @Override
    protected String buildSql() {
        SELECT("count(*) total");
        FROM(getTableName());
        buildWhereClause();
        if (StringUtils.isNotEmpty(addonWhereClause)) {
            WHERE(addonWhereClause);
        }
        return toString();
    }

    public void setAddonWhereClause(String addonWhereClause) {
        this.addonWhereClause = addonWhereClause;
    }
}
