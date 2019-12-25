package com.yixsoft.support.mybatis;

import org.mybatis.spring.mapper.MapperFactoryBean;

import java.lang.reflect.Method;

/**
 * Create by yixian at 2019-04-30 19:35
 */
public interface InterfaceMapperConfigurator {
    void config(MapperFactoryBean factory, Class mapperInterface);

    static String getMethodStatementName(Class mapperInterface, Method method) {
        return mapperInterface.getName() + "." + method.getName();
    }
}
