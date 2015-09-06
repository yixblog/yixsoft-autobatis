package cn.yixblog.support.mybatis.autosql.configuration.support.config;

import cn.yixblog.support.mybatis.autosql.annotations.AutoMapper;
import cn.yixblog.support.mybatis.autosql.annotations.AutoSql;
import cn.yixblog.support.mybatis.autosql.annotations.SqlType;
import cn.yixblog.support.mybatis.autosql.configuration.support.dialect.SqlDialectManager;
import cn.yixblog.support.mybatis.autosql.configuration.support.spring.ApplicationContextHelper;
import cn.yixblog.support.mybatis.autosql.core.IAutoSqlProvider;
import cn.yixblog.support.mybatis.autosql.dialects.ColumnInfo;
import cn.yixblog.support.mybatis.autosql.dialects.ISqlDialect;
import cn.yixblog.support.mybatis.utils.MapperMethodUtils;
import org.apache.ibatis.mapping.SqlCommandType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * config params for generation a sql
 * Created by yixian on 2015-09-02.
 */
public class SqlGenerationConfig {
    private final SqlDialectManager manager;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final String tableName;
    private final String[] pkNames;
    private Map<String, ColumnInfo> tableColumns;
    private final String dialectName;
    private final String statementId;
    private final Class resultType;
    private final SqlType type;

    public SqlGenerationConfig(String statementName, Method method) {
        statementId = statementName;
        AutoSql autoSqlConfig = method.getDeclaredAnnotation(AutoSql.class);
        AutoMapper autoMapperConfig = method.getDeclaringClass().getDeclaredAnnotation(AutoMapper.class);
        pkNames = autoMapperConfig.pkName();
        tableName = autoMapperConfig.tablename();
        resultType = MapperMethodUtils.getReturnType(method);
        type = autoSqlConfig.type();
        dialectName = autoMapperConfig.dialect();
        manager = ApplicationContextHelper.getBean(SqlDialectManager.class);
    }

    public IAutoSqlProvider newSqlProvider(Object parameterObject) {
        if (tableColumns == null) {
            loadTableColumns();
        }
        Class<? extends IAutoSqlProvider> providerType = type.getProviderClass();
        try {
            IAutoSqlProvider provider = providerType.newInstance();
            provider.setPkNames(pkNames);
            provider.setTable(tableName);
            provider.setTableColumns(tableColumns);
            provider.setDialect(manager.getDialect(dialectName));
            provider.setParameter(parameterObject);
            return provider;
        } catch (InstantiationException | IllegalAccessException e) {
            //as all sql provider was written by myself ,these exceptions shall never happen
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    private void loadTableColumns() {
        ISqlDialect dialect = manager.getDialect(dialectName);
        List<ColumnInfo> columns = dialect.selectTableColumns(tableName);
        Map<String, ColumnInfo> columnMap = new HashMap<>();
        for (ColumnInfo col : columns) {
            columnMap.put(col.getColumn().toLowerCase(), col);
        }
        tableColumns = columnMap;
    }

    public String getStatementId() {
        return statementId;
    }

    public Class getResultType() {
        return resultType;
    }

    public SqlCommandType getType() {
        return type.getCommondType();
    }


}
