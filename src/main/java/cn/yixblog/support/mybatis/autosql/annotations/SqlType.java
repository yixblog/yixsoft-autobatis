package cn.yixblog.support.mybatis.autosql.annotations;

import cn.yixblog.support.mybatis.autosql.core.IAutoSqlProvider;
import cn.yixblog.support.mybatis.autosql.core.providers.DeleteSqlProvider;
import cn.yixblog.support.mybatis.autosql.core.providers.InsertSqlProvider;
import cn.yixblog.support.mybatis.autosql.core.providers.SelectSqlProvider;
import cn.yixblog.support.mybatis.autosql.core.providers.UpdateSqlProvider;

/**
 * sql type
 * Created by yixian on 2015-09-02.
 */
public enum SqlType {
    INSERT(InsertSqlProvider.class),
    UPDATE(UpdateSqlProvider.class),
    SELECT(SelectSqlProvider.class),
    FIND_ONE(SelectSqlProvider.class),
    DELETE(DeleteSqlProvider.class);

    private Class<? extends IAutoSqlProvider> providerClass;

    SqlType(Class<? extends IAutoSqlProvider> providerClass) {
        this.providerClass = providerClass;
    }

    public Class<? extends IAutoSqlProvider> getProviderClass(){
        return providerClass;
    }
}
