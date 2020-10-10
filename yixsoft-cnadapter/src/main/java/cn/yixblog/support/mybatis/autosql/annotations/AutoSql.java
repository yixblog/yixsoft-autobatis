package cn.yixblog.support.mybatis.autosql.annotations;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * annotation declare on method,to declare this method is an auto generation sql method
 * Created by yixian on 2015-08-27.
 */
@Documented
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@com.yixsoft.support.mybatis.autosql.annotations.AutoSql
@Deprecated
public @interface AutoSql {
    @AliasFor(value = "type", annotation = com.yixsoft.support.mybatis.autosql.annotations.AutoSql.class)
    SqlType type();

    @AliasFor(value = "ignoreNull", annotation = com.yixsoft.support.mybatis.autosql.annotations.AutoSql.class)
    boolean ignoreNull() default false;
}
