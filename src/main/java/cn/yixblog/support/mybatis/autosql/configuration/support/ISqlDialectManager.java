package cn.yixblog.support.mybatis.autosql.configuration.support;

import cn.yixblog.support.mybatis.autosql.dialects.ISqlDialect;

/**
 * SqlDialectManager
 * Created by yixian on 2015-09-02.
 */
public interface ISqlDialectManager {
    ISqlDialect getDialect(String name);
}
