package com.yixsoft.support.mybatis.autosql.annotations;

import java.lang.annotation.*;

/**
 * Advanced select
 * Created by yixian on 2016-04-24.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@AutoSql(value = SqlType.SELECT)
public @interface AdvanceSelect {
    /**
     * columns declared in this param will not appears in the auto-generated sql
     * @return excluded columns in result set
     */
    String[] excludeColumns() default {};

    /**
     * add where clause for static to auto-generated sql
     * @return addition where clauses
     */
    String addonWhereClause() default "";
}
