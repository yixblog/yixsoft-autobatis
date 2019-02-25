package cn.yixblog.support.mybatis.autosql.configuration.support.config;

import cn.yixblog.support.mybatis.autosql.annotations.AdvanceSelect;
import cn.yixblog.support.mybatis.autosql.annotations.AutoMapper;
import cn.yixblog.support.mybatis.autosql.annotations.AutoSql;
import cn.yixblog.support.mybatis.autosql.annotations.SqlType;
import cn.yixblog.support.mybatis.autosql.configuration.support.dialect.SqlDialectManager;
import cn.yixblog.support.mybatis.autosql.configuration.support.spring.ApplicationContextHelper;
import cn.yixblog.support.mybatis.autosql.core.IAutoSqlProvider;
import cn.yixblog.support.mybatis.autosql.core.providers.CountSqlProvider;
import cn.yixblog.support.mybatis.autosql.core.providers.SelectSqlProvider;
import cn.yixblog.support.mybatis.autosql.dialects.ColumnInfo;
import cn.yixblog.support.mybatis.autosql.dialects.ISqlDialect;
import cn.yixblog.support.mybatis.utils.MapperMethodUtils;
import org.apache.ibatis.mapping.SqlCommandType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
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
    private final boolean pkAutoIncrement;
    private Map<String, ColumnInfo> tableColumns;
    private final String[] excludeColumns;
    private final String addonWhereClause;
    private final String dialectName;
    private final String statementId;
    private final Class resultType;
    private final SqlType type;

    public SqlGenerationConfig(String statementName, Method method) {
        statementId = statementName;
        AutoSql autoSqlConfig = method.getAnnotation(AutoSql.class);
        AdvanceSelect advanceSelect = method.getAnnotation(AdvanceSelect.class);
        if (advanceSelect != null) {
            excludeColumns = advanceSelect.excludeColumns();
            addonWhereClause = advanceSelect.addonWhereClause();
        } else {
            excludeColumns = null;
            addonWhereClause = "";
        }
        AutoMapper autoMapperConfig = method.getDeclaringClass().getAnnotation(AutoMapper.class);
        pkNames = autoMapperConfig.pkName();
        pkAutoIncrement = autoMapperConfig.pkAutoIncrement();
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
            IAutoSqlProvider provider = providerType.getDeclaredConstructor().newInstance();
            provider.setPkNames(pkNames);
            provider.setPkAutoIncrement(pkAutoIncrement);
            provider.setTable(tableName);
            provider.setTableColumns(tableColumns);
            provider.setDialect(manager.getDialect(dialectName));
            provider.setParameter(parameterObject);
            if (provider instanceof SelectSqlProvider) {
                ((SelectSqlProvider) provider).setExcludeColumns(excludeColumns);
                ((SelectSqlProvider) provider).setAddonWhereClause(addonWhereClause);
            } else if (provider instanceof CountSqlProvider) {
                ((CountSqlProvider) provider).setAddonWhereClause(addonWhereClause);
            }

            return provider;
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
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

    public boolean isPkAutoIncrement() {
        return pkAutoIncrement;
    }

    public String[] getPkNames() {
        return pkNames;
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
