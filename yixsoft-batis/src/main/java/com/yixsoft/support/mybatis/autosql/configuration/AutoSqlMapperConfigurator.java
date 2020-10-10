package com.yixsoft.support.mybatis.autosql.configuration;

import com.yixsoft.support.mybatis.InterfaceMapperConfigurator;
import com.yixsoft.support.mybatis.autosql.annotations.AutoMapper;
import com.yixsoft.support.mybatis.autosql.annotations.AutoSql;
import com.yixsoft.support.mybatis.autosql.configuration.sqlsource.AutoSqlSource;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.Method;

/**
 * Create by yixian at 2019-04-30 20:01
 */
public class AutoSqlMapperConfigurator implements InterfaceMapperConfigurator {

    @Override
    public void config(MapperFactoryBean factory, Class mapperInterface) {
        if (AnnotatedElementUtils.isAnnotated(mapperInterface, AutoMapper.class)) {
            Configuration configuration = factory.getSqlSession().getConfiguration();
            for (Method method : mapperInterface.getDeclaredMethods()) {
                String statementName = InterfaceMapperConfigurator.getMethodStatementName(mapperInterface, method);
                if (!configuration.hasStatement(statementName) && AnnotatedElementUtils.isAnnotated(method, AutoSql.class)) {
                    MappedStatement ms = getMethodStatement(factory, configuration, statementName, method);
                    configuration.addMappedStatement(ms);
                }
            }
        }
    }

    private MappedStatement getMethodStatement(MapperFactoryBean factory, Configuration configuration, String statementName, Method method) {
        AutoSqlSource sqlSource = new AutoSqlSource(factory, statementName, configuration, method);
        return sqlSource.createStatement();
    }

}
