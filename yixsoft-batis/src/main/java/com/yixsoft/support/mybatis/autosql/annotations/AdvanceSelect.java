package com.yixsoft.support.mybatis.autosql.annotations;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * Advanced select
 * Created by yixian on 2016-04-24.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.ANNOTATION_TYPE})
@AutoSql(value = SqlType.SELECT)
public @interface AdvanceSelect {
    /**
     * columns declared in this param will not appears in the auto-generated sql
     *
     * @return excluded columns in result set
     */
    String[] excludeColumns() default {};

    /**
     * add where clause for static to auto-generated sql
     *
     * @return addition where clauses
     */
    String addonWhereClause() default "";

    @AliasFor(value = "ignoreNullRule", annotation = AutoSql.class)
    IgnoreNullRule ignoreNullRule() default IgnoreNullRule.PARAM_TYPE_DETECT;
}
