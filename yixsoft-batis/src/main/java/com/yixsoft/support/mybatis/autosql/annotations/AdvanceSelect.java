package com.yixsoft.support.mybatis.autosql.annotations;

import java.lang.annotation.*;

/**
 * 高级查询
 * Created by yixian on 2016-04-24.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AdvanceSelect {
    /**
     * columns declared in this param will not appears in the auto-generated sql
     */
    String[] excludeColumns() default {};

    /**
     * add where clause for static to auto-generated sql
     */
    String addonWhereClause() default "";
}
