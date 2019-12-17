package com.yixsoft.support.mybatis.autosql.configuration.support;

import com.yixsoft.support.mybatis.autosql.configuration.IAutoSqlFactoryBean;

import java.util.List;

/**
 * caching mapper factory beans while initializing
 * Created by yixian on 2015-09-01.
 */
public interface IMapperFactoryBeanCache {

    void add(IAutoSqlFactoryBean factoryBean);

    IAutoSqlFactoryBean pull();

    List<IAutoSqlFactoryBean> getAll();

    void clear();
}
