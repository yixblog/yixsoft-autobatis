package com.yixsoft.support.mybatis.autosql.dialects;

import java.lang.annotation.*;

/**
 * Create by davep at 2019-12-25 15:23
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SupportsDatabase {
    String[] value();
}
