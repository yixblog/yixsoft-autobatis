package cn.yixblog.support.mybatis.autosql.pk;

/**
 * provide a unique string type primary key for insert
 * Created by yixian on 2015-09-02.
 */
public interface IPrimaryKeyProvider {
    String next();
}
