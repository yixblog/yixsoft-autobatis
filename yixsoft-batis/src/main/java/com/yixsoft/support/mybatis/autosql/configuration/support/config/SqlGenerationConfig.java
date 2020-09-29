package com.yixsoft.support.mybatis.autosql.configuration.support.config;

import com.yixsoft.support.mybatis.autosql.annotations.*;
import com.yixsoft.support.mybatis.autosql.core.IAutoSqlProvider;
import com.yixsoft.support.mybatis.autosql.core.providers.CountSqlProvider;
import com.yixsoft.support.mybatis.autosql.core.providers.SelectSqlProvider;
import com.yixsoft.support.mybatis.autosql.core.providers.UpdateSqlProvider;
import com.yixsoft.support.mybatis.autosql.dialects.ColumnInfo;
import com.yixsoft.support.mybatis.autosql.dialects.ISqlDialect;
import com.yixsoft.support.mybatis.autosql.dialects.SqlDialectManager;
import com.yixsoft.support.mybatis.autosql.dialects.exceptions.AutoSqlException;
import com.yixsoft.support.mybatis.utils.MapperMethodUtils;
import com.yixsoft.support.mybatis.utils.TypeUtils;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * config params for generation a sql
 * Created by yixian on 2015-09-02.
 */
public class SqlGenerationConfig {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final String tableName;
    private final String[] pkNames;
    private final Class<? extends KeyGenerator> pkProvider;
    private final Configuration configuration;
    private final MapperFactoryBean parentFactory;
    private ISqlDialect dialect;
    private Map<String, ColumnInfo> tableColumns;
    private final String[] excludeColumns;
    private final String addonWhereClause;
    private final boolean ignoreNull;
    private final String[] staticUpdates;
    private final String statementId;
    private final Class resultType;
    private final SqlType type;
    private Class<?> parameterType;

    public SqlGenerationConfig(MapperFactoryBean factory, Configuration configuration, String statementName, Method method) {
        statementId = statementName;
        this.parentFactory = factory;
        this.configuration = configuration;
        AutoSql autoSqlConfig = AnnotatedElementUtils.getMergedAnnotation(method, AutoSql.class);
        AdvanceSelect advanceSelect = AnnotatedElementUtils.getMergedAnnotation(method, AdvanceSelect.class);
        if (advanceSelect != null) {
            excludeColumns = advanceSelect.excludeColumns();
            addonWhereClause = advanceSelect.addonWhereClause();
        } else {
            excludeColumns = null;
            addonWhereClause = "";
        }
        AutoMapper autoMapperConfig = method.getDeclaringClass().getAnnotation(AutoMapper.class);
        pkNames = autoMapperConfig.pkName();
        pkProvider = autoMapperConfig.keyGenerator();
        tableName = autoMapperConfig.tablename();
        resultType = MapperMethodUtils.getReturnType(method);
        type = autoSqlConfig.value();
        ignoreNull = autoSqlConfig.ignoreNull();
        parameterType = getParameterType(method);
        StaticUpdate updateAnnotation = AnnotatedElementUtils.getMergedAnnotation(method, StaticUpdate.class);
        if (type == SqlType.UPDATE && updateAnnotation != null) {
            staticUpdates = updateAnnotation.value();
        } else {
            staticUpdates = null;
        }
    }

    public IAutoSqlProvider newSqlProvider(Object parameterObject) {
        if (tableColumns == null) {
            loadTableColumns();
        }
        Class<? extends IAutoSqlProvider> providerType = type.getProviderClass();
        try {
            IAutoSqlProvider provider = providerType.getDeclaredConstructor().newInstance();
            provider.setIgnoreNullValue(ignoreNull);
            provider.setConfiguration(configuration);
            provider.setPkNames(pkNames);
            provider.setPkProvider(pkProvider);
            provider.setTable(tableName);
            provider.setTableColumns(tableColumns);

            provider.setDialect(getDialect());
            provider.setParameter(parameterObject);
            if (provider instanceof SelectSqlProvider) {
                ((SelectSqlProvider) provider).setExcludeColumns(excludeColumns);
                ((SelectSqlProvider) provider).setAddonWhereClause(addonWhereClause);
            } else if (provider instanceof CountSqlProvider) {
                ((CountSqlProvider) provider).setAddonWhereClause(addonWhereClause);
            } else if (provider instanceof UpdateSqlProvider) {
                ((UpdateSqlProvider) provider).setStaticUpdates(staticUpdates);
            }

            return provider;
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            //as all sql provider was written by myself ,these exceptions shall never happen
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    private void loadTableColumns() {
        List<ColumnInfo> columns = getDialect().selectTableColumns(tableName);
        Map<String, ColumnInfo> columnMap = new HashMap<>();
        for (ColumnInfo col : columns) {
            columnMap.put(col.getColumn().toLowerCase(), col);
        }
        tableColumns = columnMap;
    }

    private ISqlDialect getDialect() {
        if (dialect == null) {
            try (Connection con = configuration.getEnvironment().getDataSource().getConnection()) {
                DatabaseMetaData metaData = con.getMetaData();
                dialect = SqlDialectManager.getDialect(parentFactory, metaData.getDatabaseProductName());
            } catch (SQLException e) {
                throw new AutoSqlException("failed to connect to database", e);
            }
        }
        return dialect;
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

    public Class<? extends KeyGenerator> getPkProvider() {
        return pkProvider;
    }

    public Class<?> getParameterType() {
        return parameterType;
    }

    public boolean isParameterMap() {
        return parameterType == null || Map.class.isAssignableFrom(parameterType);
    }

    private Class<?> getParameterType(Method method) {
        Class<?> parameterType = null;
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (Class<?> currentParameterType : parameterTypes) {
            if (!RowBounds.class.isAssignableFrom(currentParameterType) && !ResultHandler.class.isAssignableFrom(currentParameterType)) {
                if (parameterType == null) {
                    if (TypeUtils.isSingleType(currentParameterType) || Map.class.isAssignableFrom(currentParameterType)) {
                        parameterType = MapperMethod.ParamMap.class;
                    } else {
                        parameterType = currentParameterType;
                    }
                } else {
                    // issue #135
                    parameterType = MapperMethod.ParamMap.class;
                }
            }
        }
        return parameterType;
    }
}
