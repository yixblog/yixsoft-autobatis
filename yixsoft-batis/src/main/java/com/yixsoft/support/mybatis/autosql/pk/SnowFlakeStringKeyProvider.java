package com.yixsoft.support.mybatis.autosql.pk;

import com.yixsoft.support.mybatis.support.snowflake.StringSequence;

public class SnowFlakeStringKeyProvider extends AbstractKeyProvider<String> implements IPrimaryKeyProvider, SupportedPrefix {
    private static final StringSequence sequence = new StringSequence();
    private String prefix = "";

    @Override
    protected String nextKey() {
        return prefix + sequence.nextId();
    }

    @Override
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
