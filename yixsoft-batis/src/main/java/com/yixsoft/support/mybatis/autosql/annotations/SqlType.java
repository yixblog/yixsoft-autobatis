package com.yixsoft.support.mybatis.autosql.annotations;

import com.yixsoft.support.mybatis.autosql.core.IAutoSqlProvider;
import com.yixsoft.support.mybatis.autosql.core.providers.*;
import org.apache.ibatis.mapping.SqlCommandType;

/**
 * sql type
 * Created by yixian on 2015-09-02.
 */
public enum SqlType {
    INSERT(InsertSqlProvider.class, SqlCommandType.INSERT),
    UPDATE(UpdateSqlProvider.class, SqlCommandType.UPDATE),
    SELECT(SelectSqlProvider.class, SqlCommandType.SELECT),
    DELETE(DeleteSqlProvider.class, SqlCommandType.DELETE),
    COUNT(CountSqlProvider.class, SqlCommandType.SELECT);

    private final Class<? extends IAutoSqlProvider> providerClass;
    private final SqlCommandType commondType;

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
