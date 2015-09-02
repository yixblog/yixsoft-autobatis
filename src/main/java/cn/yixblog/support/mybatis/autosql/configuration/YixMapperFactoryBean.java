package cn.yixblog.support.mybatis.autosql.configuration;

import cn.yixblog.support.mybatis.autosql.annotations.AutoMapper;
import cn.yixblog.support.mybatis.autosql.annotations.AutoSql;
import cn.yixblog.support.mybatis.autosql.configuration.support.MapperFactoryBeanCache;
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
        MapperFactoryBeanCache.getInstance().add(this);
    }

    @Override
    public void attachAutoSqlStatements() {
        Configuration configuration = getSqlSession().getConfiguration();
        Class mapperInterface = getMapperInterface();
        if (mapperInterface.isAnnotationPresent(AutoMapper.class)) {
            for (Method method : mapperInterface.getDeclaredMethods()) {
                String statementName = getMethodStatementName(method);
                if (!configuration.hasStatement(statementName) && method.isAnnotationPresent(AutoSql.class)){

                }
            }
        }
    }

    private String getMethodStatementName(Method method) {
        return getMapperInterface().getName() + "." + method.getName();
    }
}
