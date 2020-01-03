package com.yixsoft.support.mybatis.autosql.core.providers;

import com.yixsoft.support.mybatis.autosql.core.IAutoSqlProvider;
import com.yixsoft.support.mybatis.autosql.dialects.ColumnInfo;
import com.yixsoft.support.mybatis.autosql.dialects.ISqlDialect;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.jdbc.SQL;

import java.util.*;


/**
 * global methods here
 * Created by yixian on 2015-09-05.
 */
public abstract class AbstractSqlProvider extends SQL implements IAutoSqlProvider {
    private ISqlDialect dialect;
    private String[] pkNames;
    private JSONObject param;
    private Map<String, ColumnInfo> tableColumnMap;
    private Map<String, Object> additionalParam = new HashMap<>();
    private String tableName;
    private String builtSql;
    private Class<? extends KeyGenerator> pkProvider;

    @Override
    public synchronized String getSql() {
        if (builtSql != null) {
            return builtSql;
        }
        builtSql = buildSql();
        return builtSql;
    }

    protected abstract String buildSql();

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
    public void setPkProvider(Class<? extends KeyGenerator> pkProvider) {
        this.pkProvider = pkProvider;
    }

    public Class<? extends KeyGenerator> getPkProvider() {
        return pkProvider;
    }

    @Override
    public void setTable(String tableName) {
        this.tableName = tableName;
    }

    protected void buildWhereClause() {
        if (param == null) {
            return;
        }
        Set<String> usedKeySet = new HashSet<>();
        for (Map.Entry<String, Object> entry : param.entrySet()) {
            ColumnInfo columnInfo;
            String key = entry.getKey();
            if ((columnInfo = tableColumnMap.get(key.toLowerCase())) != null && !usedKeySet.contains(key.toLowerCase())) {
                WHERE(buildParamCondition(columnInfo, key, entry.getValue()));
                //in case the object has multiple key references to same column(because key in map is case sensitive)
                usedKeySet.add(key.toLowerCase());
            }
        }
    }

    protected String buildParamCondition(ColumnInfo columnInfo, String key, Object value) {
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