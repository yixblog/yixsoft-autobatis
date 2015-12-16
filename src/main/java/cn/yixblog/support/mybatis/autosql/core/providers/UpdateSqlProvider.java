package cn.yixblog.support.mybatis.autosql.core.providers;

import cn.yixblog.support.mybatis.autosql.core.IAutoSqlProvider;
import cn.yixblog.support.mybatis.autosql.dialects.ColumnInfo;
import cn.yixblog.support.mybatis.exceptions.AutoSqlException;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.ArrayUtils;

import java.util.*;

import static org.apache.ibatis.jdbc.SqlBuilder.*;

/**
 * auto update sql builder
 * Created by yixian on 2015-09-02.
 */
public class UpdateSqlProvider extends AbstractSqlProvider implements IAutoSqlProvider {

    @Override
    public String getSql() {
        BEGIN();
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
                    whereClauses.add(getDialect().escapeKeyword(key.toUpperCase()) + "=#{" + key + "}");
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
        for (String clause : whereClauses) {
            WHERE(clause);
        }
        return SQL();
    }


}
