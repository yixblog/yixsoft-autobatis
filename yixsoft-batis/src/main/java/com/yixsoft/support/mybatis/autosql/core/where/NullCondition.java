package com.yixsoft.support.mybatis.autosql.core.where;

import java.util.Map;

public class NullCondition implements WhereCondition{
    @Override
    public String buildWhereCondition(String columnName, String key, Map<String, Object> additionalParam) {
        return "%s is null".formatted(columnName);
    }

}
