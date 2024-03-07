package com.yixsoft.support.mybatis.autosql.core.where;

import java.util.Map;

public class EqualsCondition implements WhereCondition {
    private final Object value;

    public EqualsCondition(Object value) {
        this.value = value;
    }

    @Override
    public String buildWhereCondition(String columnName, String key, Map<String, Object> additionalParam) {
        additionalParam.put("__condition_%s".formatted(key), value);
        return "%s=#{__condition_%s}".formatted(columnName, key);
    }

}
