package cn.yixblog.support.mybatis.autosql.configuration;

import cn.yixblog.support.mybatis.autosql.annotations.AutoMapper;
import cn.yixblog.support.mybatis.autosql.annotations.AutoSql;
import cn.yixblog.support.mybatis.autosql.configuration.sqlsource.AutoSqlSource;
import cn.yixblog.support.mybatis.autosql.configuration.support.IMapperFactoryBeanCache;
import cn.yixblog.support.mybatis.autosql.configuration.support.impls.MapperFactoryBeanCache;
import cn.yixblog.support.mybatis.autosql.configuration.support.spring.ApplicationContextHelper;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.mapper.MapperFactoryBean;

import java.lang.reflect.Method;

/**
 * mapper factory bean impl<br>
 * <p/>
 * register AutoSql Mapper methods into statements
 * Created by yixian on 2015-09-01.
 */
public class YixMapperFactoryBean extends MapperFactoryBean implements IAutoSqlFactoryBean {


    @Override
    protected void checkDaoConfig() {
        super.checkDaoConfig();
        ApplicationContextHelper.getBean(IMapperFactoryBeanCache.class).add(this);
    }

    @Override
    public void attachAutoSqlStatements() {
        Configuration configuration = getSqlSession().getConfiguration();
        Class mapperInterface = getMapperInterface();
        if (mapperInterface.isAnnotationPresent(AutoMapper.class)) {
            for (Method method : mapperInterface.getDeclaredMethods()) {
                String statementName = getMethodStatementName(method);
                if (!configuration.hasStatement(statementName) && method.isAnnotationPresent(AutoSql.class)) {
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

    private String getMethodStatementName(Method method) {
        return getMapperInterface().getName() + "." + method.getName();
    }
}
