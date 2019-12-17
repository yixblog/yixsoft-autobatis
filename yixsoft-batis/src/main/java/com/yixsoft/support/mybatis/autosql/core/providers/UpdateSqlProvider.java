package com.yixsoft.support.mybatis.autosql.core.providers;

import com.yixsoft.support.mybatis.autosql.core.IAutoSqlProvider;
import com.yixsoft.support.mybatis.autosql.dialects.ColumnInfo;
import com.yixsoft.support.mybatis.exceptions.AutoSqlException;
import com.alibaba.fastjson.JSONObject;
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
        JSONObject param = getParam();
        List<String> whereClauses = new ArrayList<>();
        Set<String> usedKeySet = new HashSet<>();
        for (Map.Entry<String, Object> paramItem : param.entrySet()) {
            String key = paramItem.getKey();
            //in case the object has multiple key references to same column(because key in map is case sensitive)
            if (!usedKeySet.contains(key.toLowerCase())) {
                if (ArrayUtils.contains(pkNames, key.toLowerCase())) {
                    if (paramItem.getValue() == null) {
                        throw new AutoSqlException("when building auto update sql,primary keys must not be null");
                    }
                    whereClauses.add(buildParamCondition(getTableColumnMap().get(key.toLowerCase()), key, paramItem.getValue()));
                } else {
                    ColumnInfo info = getTableColumnMap().get(key.toLowerCase());
                    if (info == null) {
                        continue;
                    }
                    String valueRefString = paramItem.getValue() == null ? "null" : "#{" + key + "}";
                    SET(getDialect().escapeKeyword(info.getColumn()) + "=" + valueRefString);
                }
                //in case the object has multiple key references to same column(because key in map is case sensitive)
                usedKeySet.add(key.toLowerCase());
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
