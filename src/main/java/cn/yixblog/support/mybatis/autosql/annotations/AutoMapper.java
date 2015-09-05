package cn.yixblog.support.mybatis.autosql.annotations;

import java.lang.annotation.*;

/**
 * 为当前Mapper指定对应表
 * Created by yixian on 2015-08-27.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoMapper {
    String tablename();

    /**
     * primary columns
     * @return pkName
     */
    String[] pkName() default {"pkid"};

    /**
     * the dialect name to use.keep null to use the default dialect in configuation
     * @return dialect name
     */
    String dialect() default "";
}
