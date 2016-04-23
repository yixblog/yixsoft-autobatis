package cn.yixblog.support.mybatis.autosql.core.providers;

import cn.yixblog.support.mybatis.autosql.core.IAutoSqlProvider;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import static org.apache.ibatis.jdbc.SqlBuilder.*;

/**
 * auto select sql builder
 * Created by yixian on 2015-09-02.
 */
public class SelectSqlProvider extends AbstractSqlProvider implements IAutoSqlProvider {

    private String[] excludeColumns;
    private String addonWhereClause;

    @Override
    public String getSql() {
        BEGIN();
        if (ArrayUtils.isEmpty(excludeColumns)) {
            SELECT("*");
        } else {
            Set<String> columns = getTableColumnMap().keySet();
            columns.removeAll(new ArrayList<>(Arrays.asList(excludeColumns)));
            SELECT(StringUtils.join(columns, ","));
        }
        FROM(getTableName());
        buildWhereClause();
        if (StringUtils.isNotEmpty(addonWhereClause)) {
            WHERE(addonWhereClause);
        }
        return SQL();
    }

    public void setExcludeColumns(String[] excludeColumns) {
        this.excludeColumns = excludeColumns;
    }

    public void setAddonWhereClause(String addonWhereClause) {
        this.addonWhereClause = addonWhereClause;
    }
}
