package com.yixsoft.support.mybatis.support.typedef;

import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class DirectFieldDescription implements FieldDescription {
    private final Field field;

    public DirectFieldDescription(Field field) {
        this.field = field;
    }

    @Override
    public String getFieldName() {
        return field.getName();
    }

    @Override
    public Object readField(Object instance) throws InvocationTargetException, IllegalAccessException {
        if (field.getDeclaringClass().isInstance(instance)) {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            return field.get(instance);
        }
        return null;
    }

    @Override
    public Class<?> getFieldType() {
        return field.getType();
    }

    @Override
    public <A extends Annotation> A findAnnotation(Class<A> annotationType) {
        return AnnotatedElementUtils.getMergedAnnotation(field, annotationType);
    }
}
