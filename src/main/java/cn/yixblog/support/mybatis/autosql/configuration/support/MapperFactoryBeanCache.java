package cn.yixblog.support.mybatis.autosql.configuration.support;

import cn.yixblog.support.mybatis.autosql.configuration.IAutoSqlFactoryBean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * caching mapper factory beans while initializing
 * Created by yixian on 2015-09-01.
 */
@Component
public class MapperFactoryBeanCache implements IMapperFactoryBeanCache {
    private static IMapperFactoryBeanCache instance;
    private Queue<IAutoSqlFactoryBean> factoryBeans;

    public static IMapperFactoryBeanCache getInstance() {
        return instance;
    }

    public MapperFactoryBeanCache() {
        factoryBeans = new ConcurrentLinkedQueue<>();
        instance = this;
    }

    @Override
    public void add(IAutoSqlFactoryBean factoryBean) {
        factoryBeans.add(factoryBean);
    }

    @Override
    public IAutoSqlFactoryBean pull(){
        return factoryBeans.poll();
    }

    @Override
    public List<IAutoSqlFactoryBean> getAll(){
        return new ArrayList<>(factoryBeans);
    }

    @Override
    public void clear(){
        factoryBeans.clear();
    }
}
