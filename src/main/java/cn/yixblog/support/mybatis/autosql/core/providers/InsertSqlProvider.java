package cn.yixblog.support.mybatis.autosql.core.providers;

import cn.yixblog.support.mybatis.autosql.core.IAutoSqlProvider;
import cn.yixblog.support.mybatis.autosql.dialects.ColumnInfo;
import com.alibaba.fastjson.JSONObject;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * sql builder for insert
 * Created by yixian on 2015-09-02.
 */
public class InsertSqlProvider extends AbstractSqlProvider implements IAutoSqlProvider {

    @Override
    protected String buildSql() {
        INSERT_INTO(getTableName());
        JSONObject param = getParam();
        attachValues(param);
        return toString();
    }

    private void attachValues(JSONObject param) {
        Set<String> usedKeySet = new HashSet<>();
        for (Map.Entry<String, Object> paramItem : param.entrySet()) {
            ColumnInfo info;
            String key = paramItem.getKey();
            if ((info = getTableColumnMap().get(key.toLowerCase())) != null && !usedKeySet.contains(key.toLowerCase())) {
                VALUES(getDialect().escapeKeyword(info.getColumn()), paramItem.getValue() == null ? "null" : "#{" + key + "}");
                //in case the object has multiple key references to same column(because key in map is case sensitive)
                usedKeySet.add(key.toLowerCase());
            }
        }
    }

}
