package cn.yixblog.support.mybatis.autosql.annotations;

import com.yixsoft.support.mybatis.autosql.annotations.AutoSql;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * Create by davep at 2019-09-09 17:15
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@com.yixsoft.support.mybatis.autosql.annotations.StaticUpdate
@Deprecated
public @interface StaticUpdate {
    @AliasFor(value = "value", annotation = com.yixsoft.support.mybatis.autosql.annotations.StaticUpdate.class)
    String[] value() default {};

    @AliasFor(value = "ignoreNull", annotation = AutoSql.class)
    boolean ignoreNull() default false;
}
