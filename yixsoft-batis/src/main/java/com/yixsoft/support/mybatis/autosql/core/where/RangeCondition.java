package com.yixsoft.support.mybatis.autosql.core.where;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RangeCondition implements WhereCondition {
    private Object lessThan;
    private boolean lessThanEquals;

    private Object greaterThan;
    private boolean greaterThanEquals;

    public RangeCondition lessThan(Object compare, boolean allowEquals) {
        this.lessThan = compare;
        this.lessThanEquals = allowEquals;
        return this;
    }

    public RangeCondition greaterThan(Object compare, boolean allowEquals) {
        this.greaterThan = compare;
        this.greaterThanEquals = allowEquals;
        return this;
    }

    @Override
    public String buildWhereCondition(String columnName, String key, Map<String, Object> additionalParam) {
        String lessThanFragment = null;
        if (lessThan != null) {
            if (lessThanEquals) {
                lessThanFragment = "%s<=#{__condition_%s_lt}".formatted(columnName, key);
            } else {
                lessThanFragment = "%s<#{__condition_%s_lt}".formatted(columnName, key);
            }
            additionalParam.put("__condition_%s_lt".formatted(key), lessThan);
        }
        String greaterThanFragment = null;
        if (greaterThan != null) {
            if (greaterThanEquals) {
                greaterThanFragment = "%s>=#{__condition_%s_gt}".formatted(columnName, key);
            } else {
                greaterThanFragment = "%s>#{__condition_%s_gt}".formatted(columnName, key);
            }
            additionalParam.put("__condition_%s_gt".formatted(key), greaterThan);
        }

        return Stream.of(lessThanFragment, greaterThanFragment)
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" and "));
    }

}
