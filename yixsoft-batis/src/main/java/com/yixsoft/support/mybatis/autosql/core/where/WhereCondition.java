package com.yixsoft.support.mybatis.autosql.core.where;

import java.util.Map;

public interface WhereCondition {
    String buildWhereCondition(String columnName, String key, Map<String, Object> additionalParam);

}
