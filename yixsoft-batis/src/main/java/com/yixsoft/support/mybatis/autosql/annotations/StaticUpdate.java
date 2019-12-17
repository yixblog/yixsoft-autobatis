package com.yixsoft.support.mybatis.autosql.annotations;

import java.lang.annotation.*;

/**
 * Create by davep at 2019-09-09 17:15
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@AutoSql(type = SqlType.UPDATE)
public @interface StaticUpdate {
    String[] value() default {};
}
