package cn.yixblog.support.mybatis.autosql.annotations;


import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * Advanced select
 * Created by yixian on 2016-04-24.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@com.yixsoft.support.mybatis.autosql.annotations.AdvanceSelect
@Deprecated
public @interface AdvanceSelect {
    /**
     * columns declared in this param will not appears in the auto-generated sql
     *
     * @return excluded columns in result set
     */
    @AliasFor(value = "excludeColumns", annotation = com.yixsoft.support.mybatis.autosql.annotations.AdvanceSelect.class)
    String[] excludeColumns() default {};

    /**
     * add where clause for static to auto-generated sql
     *
     * @return addition where clauses
     */
    @AliasFor(value = "addonWhereClause", annotation = com.yixsoft.support.mybatis.autosql.annotations.AdvanceSelect.class)
    String addonWhereClause() default "";

    @AliasFor(value = "ignoreNull", annotation = com.yixsoft.support.mybatis.autosql.annotations.AdvanceSelect.class)
    boolean ignoreNull() default false;
}
