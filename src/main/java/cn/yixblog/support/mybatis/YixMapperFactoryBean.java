package cn.yixblog.support.mybatis;

import cn.yixblog.support.mybatis.autosql.configuration.AutoSqlMapperConfigurator;
import cn.yixblog.support.mybatis.autosql.configuration.IAutoSqlFactoryBean;
import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * mapper factory bean impl<br>
 * <p/>
 * register AutoSql Mapper methods into statements
 * Created by yixian on 2015-09-01.
 */
public class YixMapperFactoryBean<T> extends MapperFactoryBean<T> implements IAutoSqlFactoryBean, FactoryBean<T> {
    private Logger log = LoggerFactory.getLogger(getClass());
    private static List<InterfaceMapperConfigurator> configurators = new ArrayList<>();

    static {
        registerConfiguration(AutoSqlMapperConfigurator.class);
    }

    public static void registerConfiguration(Class<? extends InterfaceMapperConfigurator> confClass) {
        try {
            InterfaceMapperConfigurator conf = confClass.getDeclaredConstructor().newInstance();
            configurators.add(conf);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
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
        Configuration configuration = getSqlSession().getConfiguration();
        Class mapperInterface = getMapperInterface();
        for (InterfaceMapperConfigurator conf : configurators) {
            conf.config(configuration, mapperInterface);
        }
    }

}
