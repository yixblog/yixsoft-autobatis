package com.yixsoft.support.mybatis.autosql.core.providers;

import com.yixsoft.support.mybatis.autosql.core.where.WhereCondition;

import java.util.Map;

public record WhereConditionBind(String columnName,
                                 String key,
                                 WhereCondition whereCondition) {
    public String buildWhereFragment(Map<String, Object> additionParams) {
        return whereCondition.buildWhereCondition(columnName, key, additionParams);
    }

}
