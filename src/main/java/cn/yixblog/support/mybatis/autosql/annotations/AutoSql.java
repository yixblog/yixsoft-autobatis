package cn.yixblog.support.mybatis.autosql.annotations;

import java.lang.annotation.*;

/**
 * annotation declare on method,to declare this method is an auto generation sql method
 * Created by yixian on 2015-08-27.
 */
@Documented
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoSql {
    SqlType type();
}
