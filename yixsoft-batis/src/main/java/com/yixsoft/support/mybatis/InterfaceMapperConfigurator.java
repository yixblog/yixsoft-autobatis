package com.yixsoft.support.mybatis;

import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.mybatis.spring.mapper.MapperFactoryBean;

import java.lang.reflect.Method;

/**
 * Create by yixian at 2019-04-30 19:35
 */
public interface InterfaceMapperConfigurator {
    void config(MapperFactoryBean factory, Class mapperInterface, Class<? extends KeyGenerator> defaultKeyGen);

    static String getMethodStatementName(Class mapperInterface, Method method) {
        return mapperInterface.getName() + "." + method.getName();
    }
}
