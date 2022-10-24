package com.yixsoft.support.mybatis.support.typedef;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;

public interface FieldInvoker {

    String getFieldName();

    Object readField(Object instance) throws InvocationTargetException, IllegalAccessException;

    <A extends Annotation> A findAnnotation(Class<A> annotationType);
}
