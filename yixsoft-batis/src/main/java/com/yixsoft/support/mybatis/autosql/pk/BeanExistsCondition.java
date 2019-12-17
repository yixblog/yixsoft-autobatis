package com.yixsoft.support.mybatis.autosql.pk;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Created by yixian on 2015-09-06.
 */
public class BeanExistsCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return context.getBeanFactory().getBeansOfType(IPrimaryKeyProvider.class).isEmpty();
    }
}
