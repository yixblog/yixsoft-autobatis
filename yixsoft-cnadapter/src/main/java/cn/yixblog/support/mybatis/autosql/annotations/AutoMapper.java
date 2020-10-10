package cn.yixblog.support.mybatis.autosql.annotations;

import com.yixsoft.support.mybatis.autosql.pk.UUIDPkProvider;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 为当前Mapper指定对应表
 * Created by yixian on 2015-08-27.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@com.yixsoft.support.mybatis.autosql.annotations.AutoMapper
@Deprecated
public @interface AutoMapper {
    @AliasFor(value = "tablename", annotation = com.yixsoft.support.mybatis.autosql.annotations.AutoMapper.class)
    String tablename();

    /**
     * primary columns
     *
     * @return pkName
     */
    @AliasFor(value = "pkName", annotation = com.yixsoft.support.mybatis.autosql.annotations.AutoMapper.class)
    String[] pkName();

    /**
     * if the primary key auto increment
     *
     * @return boolean value
     */
    @AliasFor(value = "keyGenerator", annotation = com.yixsoft.support.mybatis.autosql.annotations.AutoMapper.class)
    Class<? extends KeyGenerator> keyGenerator() default UUIDPkProvider.class;

}
