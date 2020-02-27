package com.yixsoft.support.mybatis.autosql.core.providers;

import com.yixsoft.support.mybatis.autosql.core.IAutoSqlProvider;
import com.yixsoft.support.mybatis.autosql.dialects.ColumnInfo;
import com.yixsoft.support.mybatis.autosql.dialects.exceptions.AutoSqlException;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;

/**
 * auto update sql builder
 * Created by yixian on 2015-09-02.
 */
public class UpdateSqlProvider extends AbstractSqlProvider implements IAutoSqlProvider {
    private String[] staticUpdates;

    @Override
    protected String buildSql() {
        UPDATE(getTableName());
        String[] pkNames = getPkNames();
        Map<String, Object> param = getParam();
        List<String> whereClauses = new ArrayList<>();
        Set<String> usedKeySet = new HashSet<>();
        for (Map.Entry<String, Object> paramItem : param.entrySet()) {
            String field = paramItem.getKey();
            String column = findFieldReferColumn(field);
            //in case the object has multiple key references to same column(because key in map is case sensitive)
            if (!usedKeySet.contains(column)) {
                if (ArrayUtils.contains(pkNames, column)) {
                    if (paramItem.getValue() == null) {
                        throw new AutoSqlException("when building auto update sql,primary keys must not be null");
                    }
                    whereClauses.add(buildParamCondition(getTableColumnMap().get(column), field, paramItem.getValue()));
                } else {
                    ColumnInfo info = getTableColumnMap().get(column);
                    if (info == null) {
                        continue;
                    }
                    String valueRefString = paramItem.getValue() == null ? "null" : "#{" + field + "}";
                    SET(getDialect().escapeKeyword(info.getColumn()) + "=" + valueRefString);
                }
                //in case the object has multiple key references to same column(because key in map is case sensitive)
                usedKeySet.add(column);
            }
        }
        if (whereClauses.size() < pkNames.length) {
            throw new AutoSqlException("when building auto update sql,primary keys must not be null");
        }
        if (staticUpdates != null) {
            for (String updateFragment : staticUpdates) {
                SET(updateFragment);
            }
        }
        for (String clause : whereClauses) {
            WHERE(clause);
        }
        return toString();
    }


    public UpdateSqlProvider setStaticUpdates(String[] staticUpdates) {
        this.staticUpdates = staticUpdates;
        return this;
    }


}
