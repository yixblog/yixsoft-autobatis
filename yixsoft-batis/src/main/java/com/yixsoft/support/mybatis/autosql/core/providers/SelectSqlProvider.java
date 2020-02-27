package com.yixsoft.support.mybatis.autosql.core.providers;

import com.yixsoft.support.mybatis.autosql.core.IAutoSqlProvider;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;


/**
 * auto select sql builder
 * Created by yixian on 2015-09-02.
 */
public class SelectSqlProvider extends AbstractSqlProvider implements IAutoSqlProvider {

    private String[] excludeColumns;
    private String addonWhereClause;

    @Override
    protected String buildSql() {
        Set<String> columns = getTableColumnMap().keySet();
        if (ArrayUtils.isNotEmpty(excludeColumns)) {
            columns.removeAll(new ArrayList<>(Arrays.asList(excludeColumns)));
        }
        SELECT(StringUtils.join(columns, ","));
        FROM(getTableName());
        buildWhereClause();
        if (StringUtils.isNotEmpty(addonWhereClause)) {
            WHERE(addonWhereClause);
        }
        return toString();
    }

    public void setExcludeColumns(String[] excludeColumns) {
        this.excludeColumns = excludeColumns;
    }

    public void setAddonWhereClause(String addonWhereClause) {
        this.addonWhereClause = addonWhereClause;
    }
}
