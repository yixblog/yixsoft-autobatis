package com.yixsoft.support.mybatis.autosql.pk;

import java.util.UUID;

/**
 * generate primary key using uuid api
 * Created by yixian on 2015-09-02.
 */
public class UUIDPkProvider extends AbstractKeyProvider<String> implements IPrimaryKeyProvider {

    @Override
    protected String nextKey() {
        return UUID.randomUUID().toString();
    }
}
