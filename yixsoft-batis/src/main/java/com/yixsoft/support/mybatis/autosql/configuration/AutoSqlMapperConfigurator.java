package com.yixsoft.support.mybatis.autosql.configuration;

import com.yixsoft.support.mybatis.InterfaceMapperConfigurator;
import com.yixsoft.support.mybatis.autosql.annotations.AutoMapper;
import com.yixsoft.support.mybatis.autosql.annotations.AutoSql;
import com.yixsoft.support.mybatis.autosql.configuration.sqlsource.AutoSqlSource;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.Method;

/**
 * Create by yixian at 2019-04-30 20:01
 */
public class AutoSqlMapperConfigurator implements InterfaceMapperConfigurator {

    @Override
    public void config(Configuration configuration, Class mapperInterface) {
        if (mapperInterface.isAnnotationPresent(AutoMapper.class)) {
            for (Method method : mapperInterface.getDeclaredMethods()) {
                String statementName = InterfaceMapperConfigurator.getMethodStatementName(mapperInterface, method);
                if (!configuration.hasStatement(statementName) && AnnotatedElementUtils.isAnnotated(method, AutoSql.class)) {
                    MappedStatement ms = getMethodStatement(configuration, statementName, method);
                    configuration.addMappedStatement(ms);
                }
            }
        }
    }

    private MappedStatement getMethodStatement(Configuration configuration, String statementName, Method method) {
        AutoSqlSource sqlSource = new AutoSqlSource(statementName, configuration, method);
        return sqlSource.createStatement();
    }

}
