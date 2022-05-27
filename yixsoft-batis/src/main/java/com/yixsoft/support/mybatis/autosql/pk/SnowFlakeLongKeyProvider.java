package com.yixsoft.support.mybatis.autosql.pk;

import com.yixsoft.support.mybatis.support.snowflake.ljf.Sequence;

public class SnowFlakeLongKeyProvider extends AbstractKeyProvider<Long> implements IPrimaryKeyProvider{
    private static final Sequence sequence = new Sequence();
    @Override
    protected Long nextKey() {
        return sequence.nextId();
    }
}
