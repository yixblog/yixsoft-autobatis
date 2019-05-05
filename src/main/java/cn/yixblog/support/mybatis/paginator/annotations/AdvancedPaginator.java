package cn.yixblog.support.mybatis.paginator.annotations;

import java.lang.annotation.*;

/**
 * Create by yixian at 2019-05-03 0:03
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.ANNOTATION_TYPE})
public @interface AdvancedPaginator {
    String countRef() default "";
    String countSql() default "";
}
