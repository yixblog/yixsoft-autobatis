package cn.yixblog.support.mybatis.paginator.annotations;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * Create by yixian at 2019-05-03 0:02
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@AdvancedPaginator
public @interface CountSqlTpl {
    @AliasFor(attribute = "countSql", annotation = AdvancedPaginator.class)
    String value();
}
