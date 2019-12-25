package com.yixsoft.support.mybatis.autosql.dialects;

import org.mybatis.spring.SqlSessionTemplate;

/**
 * Create by davep at 2019-12-24 16:12
 */
public interface SqlDialectConfigurer<D extends ISqlDialect> {

    D getDialect(SqlSessionTemplate template);

    String[] additionMapperLocations();
}
