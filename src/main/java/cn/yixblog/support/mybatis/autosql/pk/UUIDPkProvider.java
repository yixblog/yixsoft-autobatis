package cn.yixblog.support.mybatis.autosql.pk;

import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * generate primary key using uuid api
 * Created by yixian on 2015-09-02.
 */
@Component
@Conditional(BeanExistsCondition.class)
public class UUIDPkProvider implements IPrimaryKeyProvider {
    @Override
    public String next() {
        return UUID.randomUUID().toString();
    }
}
