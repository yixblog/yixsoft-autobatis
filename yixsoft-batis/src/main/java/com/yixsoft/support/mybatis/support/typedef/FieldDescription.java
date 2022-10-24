package com.yixsoft.support.mybatis.support.typedef;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;

public interface FieldDescription {

    String getFieldName();

    Object readField(Object instance) throws InvocationTargetException, IllegalAccessException;

    Class<?> getFieldType();

    <A extends Annotation> A findAnnotation(Class<A> annotationType);
}
