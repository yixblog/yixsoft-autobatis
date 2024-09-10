package com.yixsoft.support.mybatis.autosql.core.providers;

import com.yixsoft.support.mybatis.autosql.dialects.entities.RowNumberConfig;
import com.yixsoft.support.mybatis.autosql.core.IAutoSqlProvider;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;


/**
 * auto select sql builder
 * Created by yixian on 2015-09-02.
 */
public class SelectSqlProvider extends AbstractSqlProvider implements IAutoSqlProvider {

    private String[] excludeColumns;
    private String addonWhereClause;
    private RowNumberConfig[] rowNumberConfigs;

    @Override
    protected String buildSql() {
        Set<String> columns = new HashSet<>(getTableColumnMap().keySet());
        if (ArrayUtils.isNotEmpty(excludeColumns)) {
            Arrays.asList(excludeColumns).forEach(columns::remove);
        }
        List<String> selectColumns = new ArrayList<>(columns.stream().map(col -> getDialect().escapeKeyword(col)).toList());
        if (rowNumberConfigs!=null) {
            for (RowNumberConfig rowNumberConfig : rowNumberConfigs) {
                if (rowNumberConfig!=null && rowNumberConfig.hasConfig()) {
                    selectColumns.add(getDialect().rowNumberFragment(rowNumberConfig));
                }
            }
        }
        SELECT(String.join(",", selectColumns));
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

    public SelectSqlProvider setRowNumberConfigs(RowNumberConfig[] rowNumberConfig) {
        this.rowNumberConfigs = rowNumberConfig;
        return this;
    }
}
