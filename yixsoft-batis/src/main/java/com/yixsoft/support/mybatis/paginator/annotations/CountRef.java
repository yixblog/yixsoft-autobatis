package com.yixsoft.support.mybatis.paginator.annotations;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 关联统计查询的statement，注意参数相同
 * Create by yixian at 2019-04-30 18:26
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@AdvancedPaginator
public @interface CountRef {
    @AliasFor(annotation = AdvancedPaginator.class, attribute = "countRef")
    String value();
}
