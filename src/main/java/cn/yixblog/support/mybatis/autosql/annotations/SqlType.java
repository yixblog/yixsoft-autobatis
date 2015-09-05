package cn.yixblog.support.mybatis.autosql.annotations;

import cn.yixblog.support.mybatis.autosql.core.IAutoSqlProvider;
import cn.yixblog.support.mybatis.autosql.core.providers.DeleteSqlProvider;
import cn.yixblog.support.mybatis.autosql.core.providers.InsertSqlProvider;
import cn.yixblog.support.mybatis.autosql.core.providers.SelectSqlProvider;
import cn.yixblog.support.mybatis.autosql.core.providers.UpdateSqlProvider;
import org.apache.ibatis.mapping.SqlCommandType;

/**
 * sql type
 * Created by yixian on 2015-09-02.
 */
public enum SqlType {
    INSERT(InsertSqlProvider.class, SqlCommandType.INSERT),
    UPDATE(UpdateSqlProvider.class, SqlCommandType.UPDATE),
    SELECT(SelectSqlProvider.class, SqlCommandType.SELECT),
    FIND_ONE(SelectSqlProvider.class, SqlCommandType.SELECT),
    DELETE(DeleteSqlProvider.class, SqlCommandType.DELETE);

    private Class<? extends IAutoSqlProvider> providerClass;
    private SqlCommandType commondType;

    SqlType(Class<? extends IAutoSqlProvider> providerClass, SqlCommandType commondType) {
        this.providerClass = providerClass;
        this.commondType = commondType;
    }

    public Class<? extends IAutoSqlProvider> getProviderClass() {
        return providerClass;
    }

    public SqlCommandType getCommondType() {
        return commondType;
    }
}
