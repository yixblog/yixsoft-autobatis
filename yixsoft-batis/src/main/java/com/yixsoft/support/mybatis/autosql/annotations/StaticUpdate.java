package com.yixsoft.support.mybatis.autosql.annotations;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * Create by davep at 2019-09-09 17:15
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@AutoSql(value = SqlType.UPDATE)
public @interface StaticUpdate {
    String[] value() default {};

    @AliasFor(value = "ignoreNullRule", annotation = AutoSql.class)
    IgnoreNullRule ignoreNullRule() default IgnoreNullRule.PARAM_TYPE_DETECT;
}
