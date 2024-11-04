package com.yixsoft.support.mybatis.autosql.core.where;

import java.util.Map;

public class LikeCondition implements WhereCondition {
    private final String value;

    public LikeCondition(String value, boolean isPrefix, boolean isSuffix) {
        String prefix = isPrefix ? "%" : "";
        String suffix = isSuffix ? "%" : "";
        this.value = prefix + value + suffix;
    }

    @Override
    public String buildWhereCondition(String columnName, String key, Map<String, Object> additionalParam) {
        additionalParam.put("__condition_%s".formatted(key), value);
        return "%s like #{__condition_%s}".formatted(columnName, key);
    }
}
