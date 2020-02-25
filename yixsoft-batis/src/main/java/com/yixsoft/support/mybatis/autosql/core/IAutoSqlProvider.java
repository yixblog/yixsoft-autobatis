package com.yixsoft.support.mybatis.autosql.core;

import com.yixsoft.support.mybatis.autosql.dialects.ColumnInfo;
import com.yixsoft.support.mybatis.autosql.dialects.ISqlDialect;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.session.Configuration;

import java.util.Map;

/**
 * sql provider
 * Created by yixian on 2015-09-02.
 */
public interface IAutoSqlProvider {
    Map<String, Object> getAdditionalParams();

    String getSql();

    void setParameter(Object parameterObject);

    void setTableColumns(Map<String, ColumnInfo> tableColumns);

    void setConfiguration(Configuration configuration);

    void setDialect(ISqlDialect dialect);

    void setPkNames(String[] pkNames);

    void setTable(String tableName);

    void setPkProvider(Class<? extends KeyGenerator> pkProvider);
}
