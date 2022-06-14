package com.yixsoft.support.mybatis.autosql.core.providers;

import com.yixsoft.support.mybatis.autosql.annotations.IgnoreNullRule;
import com.yixsoft.support.mybatis.autosql.core.IAutoSqlProvider;
import com.yixsoft.support.mybatis.autosql.dialects.ColumnInfo;
import com.yixsoft.support.mybatis.autosql.dialects.ISqlDialect;
import com.yixsoft.support.mybatis.utils.ClassFieldsDescription;
import com.yixsoft.support.mybatis.utils.FieldDescription;
import com.yixsoft.support.mybatis.utils.TypeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.jdbc.SQL;
import org.apache.ibatis.session.Configuration;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * global methods here
 * Created by yixian on 2015-09-05.
 */
public abstract class AbstractSqlProvider extends SQL implements IAutoSqlProvider {
    private static final Map<Class<?>, ClassFieldsDescription<?>> classDescCache = new ConcurrentHashMap<>();
    private Configuration configuration;
    private ISqlDialect dialect;
    private String[] pkNames;
    private ClassFieldsDescription<?> classDesc;
    private Map<String, FieldDescription> fieldReferences;
    private Map<String, Object> param;
    private Map<String, ColumnInfo> tableColumnMap;
    private final Map<String, Object> additionalParam = new HashMap<>();
    private String tableName;
    private String builtSql;
    private Class<? extends KeyGenerator> pkProvider;
    private IgnoreNullRule ignoreNullRule;

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
        Map<String, Object> paramObj;
        if (parameterObject == null) {
            paramObj = null;
        } else if (TypeUtils.isSingleType(parameterObject.getClass())) {
            paramObj = new HashMap<>();
            if (pkNames.length == 1) {
                paramObj.put(pkNames[0], parameterObject);
            }
        } else if (parameterObject instanceof Map) {
            boolean ignoreNull = ignoreNullRule == IgnoreNullRule.ALWAYS;
            paramObj = ((Map<?, ?>) parameterObject).entrySet()
                    .stream()
                    .filter(entry -> entry.getKey() != null)
                    .filter(entry -> !ignoreNull || entry.getValue() != null)
                    .collect(HashMap::new, (map, entry) -> map.put(entry.getKey().toString(), entry.getValue()), HashMap::putAll);
        } else {
            boolean ignoreNull = ignoreNullRule != IgnoreNullRule.NEVER;
            this.classDesc = describeClass(parameterObject.getClass());
            this.fieldReferences = classDesc.mapFields(tableColumnMap.keySet());
            paramObj = classDesc.convertToMap(parameterObject, ignoreNull);
        }
        this.param = paramObj;
    }

    protected ClassFieldsDescription<?> describeClass(Class<?> type) {
        return classDescCache.computeIfAbsent(type, ClassFieldsDescription::new);
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
            String columnName = findFieldReferColumn(entry.getKey());

            if ((columnInfo = tableColumnMap.get(columnName)) != null && !usedKeySet.contains(columnName)) {
                WHERE(buildParamCondition(columnInfo, columnName, entry.getValue()));
                //in case the object has multiple key references to same column(because key in map is case sensitive)
                usedKeySet.add(columnName);
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

    protected String findFieldReferColumn(String fieldName) {
        if (fieldReferences != null) {
            return fieldReferences.values().stream()
                    .filter(field -> field.getFieldName().equals(fieldName))
                    .map(FieldDescription::getMatchingColumn)
                    .findFirst().orElse(fieldName);
        }
        return fieldName;
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

    protected ISqlDialect getDialect() {
        return dialect;
    }

    protected String[] getPkNames() {
        return pkNames;
    }

    protected Map<String, Object> getParam() {
        return param;
    }

    protected Map<String, ColumnInfo> getTableColumnMap() {
        return tableColumnMap;
    }

    protected String getTableName() {
        return dialect.escapeKeyword(tableName);
    }

    @Override
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public void setIgnoreNullRule(IgnoreNullRule ignore) {
        this.ignoreNullRule = ignore;
    }
}
