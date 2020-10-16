package com.yixsoft.support.mybatis.autosql.dialects;

import com.yixsoft.support.mybatis.autosql.dialects.exceptions.AutoSqlException;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.BeanUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * dialect manager impl
 * Created by yixian on 2015-09-02.
 */
public class SqlDialectManager {
    private SqlDialectManager(){}
    private static final Map<String, Class<? extends ISqlDialect>> dialectMap = new HashMap<>();

    public static synchronized ISqlDialect getDialect(MapperFactoryBean parentFactory, String name) {
        Class<? extends ISqlDialect> clazz = findDialect(name.toLowerCase());
        if (clazz == null) {
            throw new AutoSqlException("Dialect not found:" + name);
        }
        ISqlDialect dialect = BeanUtils.instantiateClass(clazz);
        dialect.init(parentFactory);
        return dialect;
    }

    private static Class<? extends ISqlDialect> findDialect(String name) {
        if (dialectMap.size() == 1) {
            return dialectMap.values().stream().findFirst().orElse(null);
        }
        return StringUtils.isEmpty(name) ? dialectMap.get(name.toLowerCase()) : dialectMap.values().stream().findFirst().orElse(null);
    }

    public static void register(String dialectName, Class<? extends ISqlDialect> sqlDialectClass) {
        dialectMap.put(dialectName.toLowerCase(), sqlDialectClass);
    }

}
