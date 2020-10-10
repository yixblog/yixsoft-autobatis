package com.yixsoft.support.mybatis.autosql.annotations;

import java.lang.annotation.*;

/**
 * Create by davep at 2020-02-27 12:09
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface Column {
    String value() default "";

}
