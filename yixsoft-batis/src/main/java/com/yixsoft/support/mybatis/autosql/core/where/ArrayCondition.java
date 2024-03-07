package com.yixsoft.support.mybatis.autosql.core.where;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ArrayCondition implements WhereCondition {
    private final Object[] array;

    public ArrayCondition(Object[] array) {
        this.array = array;
    }

    public ArrayCondition(Collection collection) {
        this.array = collection.toArray();
    }


    @Override
    public String buildWhereCondition(String columnName, String key, Map<String, Object> additionalParam) {
        List<String> inConditions = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            if (array[i] == null) {
                continue;
            }
            String additionalKey = "autoextend_%s_%d".formatted(key, i);
            inConditions.add("#{%s}".formatted(additionalKey));
            additionalParam.put(additionalKey, array[i]);
        }
        return "%s in (%s)".formatted(columnName, String.join(",", inConditions));
    }

}
