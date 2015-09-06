package cn.yixblog.support.mybatis.autosql.core.providers;

import cn.yixblog.support.mybatis.autosql.core.IAutoSqlProvider;
import cn.yixblog.support.mybatis.autosql.dialects.ColumnInfo;
import cn.yixblog.support.mybatis.autosql.dialects.ISqlDialect;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.util.*;

import static org.apache.ibatis.jdbc.SqlBuilder.WHERE;

/**
 * global methods here
 * Created by yixian on 2015-09-05.
 */
public abstract class AbstractSqlProvider implements IAutoSqlProvider {
    private ISqlDialect dialect;
    private String[] pkNames;
    private JSONObject param;
    private Map<String, ColumnInfo> tableColumnMap;
    private Map<String, Object> additionalParam = new HashMap<>();
    private String tableName;

    @Override
    public void setParameter(Object parameterObject) {
        JSONObject paramObj;
        if (parameterObject == null) {
            paramObj = null;
        } else if (isWrapperType(parameterObject.getClass()) || parameterObject instanceof CharSequence) {
            paramObj = new JSONObject();
            if (pkNames.length == 1) {
                paramObj.put(pkNames[0], parameterObject);
            }
        } else if (parameterObject instanceof JSONObject) {
            paramObj = (JSONObject) parameterObject;
        } else {
            paramObj = (JSONObject) JSONObject.toJSON(parameterObject);
        }
        this.param = paramObj;
    }

    @Override
    public Map<String, Object> getAdditionalParams() {
        return additionalParam;
    }

    @Override
    public void setTableColumns(Map<String, ColumnInfo> tableColumns) {
        this.tableColumnMap = tableColumns;
    }

    @Override
    public void setDialect(ISqlDialect dialect) {
        this.dialect = dialect;
    }

    @Override
    public void setPkNames(String[] pkNames) {
        String[] lowerNames = new String[pkNames.length];
        for (int i = 0; i < pkNames.length; i++) {
            lowerNames[i] = pkNames[i].toLowerCase();
        }
        this.pkNames = lowerNames;
    }

    @Override
    public void setTable(String tableName) {
        this.tableName = tableName;
    }

    protected void buildWhereClause() {
        if (param == null) {
            return;
        }
        for (Map.Entry<String, Object> entry : param.entrySet()) {
            ColumnInfo columnInfo;
            if ((columnInfo = tableColumnMap.get(entry.getKey().toLowerCase())) != null) {
                WHERE(buildParamCondition(columnInfo, entry.getKey(), entry.getValue()));
            }
        }
    }

    private String buildParamCondition(ColumnInfo columnInfo, String key, Object value) {
        StringBuilder builder = new StringBuilder(dialect.escapeKeyword(columnInfo.getColumn()));
        if (value == null) {
            builder.append(" is null");
        } else if (value.getClass().isArray()) {
            Object[] array = (Object[]) value;
            buildInConditions(key, builder, array);
        } else if (value instanceof Collection) {
            Object[] array = ((Collection) value).toArray();
            buildInConditions(key, builder, array);
        } else {
            builder.append("=#{").append(key).append("}");
        }
        return builder.toString();
    }

    private void buildInConditions(String key, StringBuilder builder, Object[] array) {
        List<String> inConditions = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            if (array[i] == null) {
                continue;
            }
            String additionalKey = "autoextend_" + key + "_" + i;
            inConditions.add("#{" + additionalKey + "}");
            additionalParam.put(additionalKey, array[i]);
        }
        builder.append(" in (").append(StringUtils.join(inConditions, ",")).append(")");
    }

    public static boolean isWrapperType(Class<?> type) {
        Class[] wrapperTypes = {Integer.class, Double.class, Float.class, Boolean.class, Byte.class, Short.class, Long.class, Character.class};
        return type.isPrimitive() || ArrayUtils.contains(wrapperTypes, type);
    }

    protected ISqlDialect getDialect() {
        return dialect;
    }

    protected String[] getPkNames() {
        return pkNames;
    }

    protected JSONObject getParam() {
        return param;
    }

    protected Map<String, ColumnInfo> getTableColumnMap() {
        return tableColumnMap;
    }

    protected String getTableName() {
        return dialect.escapeKeyword(tableName);
    }
}
