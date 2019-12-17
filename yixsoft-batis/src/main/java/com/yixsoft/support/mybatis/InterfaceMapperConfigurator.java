package com.yixsoft.support.mybatis;

import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Method;

/**
 * Create by yixian at 2019-04-30 19:35
 */
public interface InterfaceMapperConfigurator {
    void config(Configuration configuration, Class mapperInterface);

    static String getMethodStatementName(Class mapperInterface, Method method) {
        return mapperInterface.getName() + "." + method.getName();
    }
}
