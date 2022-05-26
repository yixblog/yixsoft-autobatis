package com.yixsoft.support.mybatis;

import com.yixsoft.support.mybatis.autosql.configuration.AutoSqlMapperConfigurator;
import com.yixsoft.support.mybatis.autosql.configuration.IAutoSqlFactoryBean;
import com.yixsoft.support.mybatis.autosql.dialects.exceptions.AutoSqlException;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * mapper factory bean impl<br>
 * <br>
 * register AutoSql Mapper methods into statements
 * Created by yixian on 2015-09-01.
 */
public class YixMapperFactoryBean<T> extends MapperFactoryBean<T> implements IAutoSqlFactoryBean, FactoryBean<T> {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private static final List<InterfaceMapperConfigurator> configurators = new ArrayList<>();

    static {
        registerConfiguration(AutoSqlMapperConfigurator.class);
    }

    public static void registerConfiguration(Class<? extends InterfaceMapperConfigurator> confClass) {
        try {
            InterfaceMapperConfigurator conf = confClass.getDeclaredConstructor().newInstance();
            configurators.add(conf);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new AutoSqlException(e);
        }
    }

    public YixMapperFactoryBean() {
    }

    public YixMapperFactoryBean(Class<T> mapperInterface) {
        super(mapperInterface);
    }

    @Override
    protected void checkDaoConfig() {
        super.checkDaoConfig();
        String names = String.join(",", getSqlSession().getConfiguration().getMappedStatementNames());
        log.debug("statement names:{}", names);
        attachAutoSqlStatements();
    }

    @Override
    public void attachAutoSqlStatements() {
        Class mapperInterface = getMapperInterface();
        for (InterfaceMapperConfigurator conf : configurators) {
            conf.config(this, mapperInterface);
        }
    }

}
