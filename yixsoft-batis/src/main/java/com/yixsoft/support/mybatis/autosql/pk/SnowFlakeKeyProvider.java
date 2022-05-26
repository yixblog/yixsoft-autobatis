package com.yixsoft.support.mybatis.autosql.pk;

import com.yixsoft.support.mybatis.support.snowflake.Sequence;

public class SnowFlakeKeyProvider extends AbstractKeyProvider<Long> implements IPrimaryKeyProvider{
    private static final Sequence sequence = new Sequence();
    @Override
    protected Long nextKey() {
        return sequence.nextId();
    }
}
