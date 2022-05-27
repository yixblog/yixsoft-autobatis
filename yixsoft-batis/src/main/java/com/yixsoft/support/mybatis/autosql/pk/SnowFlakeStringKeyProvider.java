package com.yixsoft.support.mybatis.autosql.pk;

import com.yixsoft.support.mybatis.support.snowflake.StringSequence;
import com.yixsoft.support.mybatis.support.snowflake.ljf.Sequence;

public class SnowFlakeStringKeyProvider extends AbstractKeyProvider<String> implements IPrimaryKeyProvider{
    private static final StringSequence sequence = new StringSequence();
    @Override
    protected String nextKey() {
        return sequence.nextId();
    }
}
