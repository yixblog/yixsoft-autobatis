package cn.yixblog.support.mybatis.autosql.core.providers;

import cn.yixblog.support.mybatis.autosql.configuration.support.spring.ApplicationContextHelper;
import cn.yixblog.support.mybatis.autosql.core.IAutoSqlProvider;
import cn.yixblog.support.mybatis.autosql.dialects.ColumnInfo;
import cn.yixblog.support.mybatis.autosql.pk.IPrimaryKeyProvider;
import com.alibaba.fastjson.JSONObject;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;

/**
 * sql builder for insert
 * Created by yixian on 2015-09-02.
 */
public class InsertSqlProvider extends AbstractSqlProvider implements IAutoSqlProvider {

    @Override
    public String getSql() {
        IPrimaryKeyProvider provider = ApplicationContextHelper.getBean(IPrimaryKeyProvider.class);
        BEGIN();
        INSERT_INTO(getTableName());
        JSONObject param = getParam();
        checkAndAttachPK(provider, param);
        attachValues(param);
        return SQL();
    }

    private void attachValues(JSONObject param) {
        for (Map.Entry<String, Object> paramItem : param.entrySet()) {
            ColumnInfo info;
            if ((info = getTableColumnMap().get(paramItem.getKey().toLowerCase())) != null) {
                VALUES(getDialect().escapeKeyword(info.getColumn()), paramItem.getValue() == null ? "null" : "#{" + paramItem.getKey() + "}");
            }
        }
    }

    private void checkAndAttachPK(IPrimaryKeyProvider provider, JSONObject param) {
        String[] pkNames = getPkNames();
        if (pkNames.length != 1) {
            return;
        }
        String pkName = pkNames[0];
        for (Map.Entry<String, Object> item : param.entrySet()) {
            if (pkName.equalsIgnoreCase(item.getKey()) && item.getValue() != null) {
                return;
            }
        }
        param.put(pkName, provider.next());
    }
}
