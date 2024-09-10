package com.yixsoft.support.mybatis.autosql.dialects.entities;

import org.apache.commons.lang3.StringUtils;

public record RowNumberConfig(String asColumn,
                              String over) {
    public static final RowNumberConfig NONE = new RowNumberConfig(null, null);

    public boolean hasConfig() {
        return StringUtils.isNotEmpty(asColumn);
    }
}
