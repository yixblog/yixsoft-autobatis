package cn.yixblog.support.mybatis.autosql.pk;

import java.util.UUID;

/**
 * generate primary key using uuid api
 * Created by yixian on 2015-09-02.
 */
public class UUIDPkProvider implements IPrimaryKeyProvider{
    @Override
    public String next() {
        return UUID.randomUUID().toString();
    }
}
