package com.yixsoft.support.mybatis.autosql.pk;

import java.util.UUID;

/**
 * generate primary key using uuid api
 * Created by yixian on 2015-09-02.
 */
public class UUIDPkProvider extends AbstractKeyProvider<String> implements IPrimaryKeyProvider, SupportedPrefix {

    private String prefix = "";

    @Override
    protected String nextKey() {
        return prefix + UUID.randomUUID();
    }

    @Override
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
