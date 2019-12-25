package com.yixsoft.support.mybatis.autosql.annotations;

import com.yixsoft.support.mybatis.autosql.pk.UUIDPkProvider;
import org.apache.ibatis.executor.keygen.KeyGenerator;

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
     *
     * @return pkName
     */
    String[] pkName();

    /**
     * if the primary key auto increment
     *
     * @return boolean value
     */
    Class<? extends KeyGenerator> keyGenerator() default UUIDPkProvider.class;

}
