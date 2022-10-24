package com.yixsoft.support.mybatis.support.typedef;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static java.util.Locale.ENGLISH;

public class GetterMethodFieldDescription implements FieldDescription{
    private final Method method;
    private final String fieldName;
    private final String readMethodName;

    public GetterMethodFieldDescription(Method method) {
        this.method = method;
        this.readMethodName = method.getName();
        this.fieldName = capitalize(readMethodName.replaceFirst("(get|is)", ""));
    }


    private static String capitalize(String name) {
        return name.substring(0, 1).toLowerCase(ENGLISH) + name.substring(1);
    }

    @Override
    public String getFieldName() {
        return fieldName;
    }

    public String getReadMethodName() {
        return readMethodName;
    }

    @Override
    public Object readField(Object instance) throws InvocationTargetException, IllegalAccessException {
        return method.invoke(instance);
    }

    @Override
    public Class<?> getFieldType() {
        return method.getReturnType();
    }

    @Override
    public <A extends Annotation> A findAnnotation(Class<A> annotationClass){
        Class<?> dClass = method.getDeclaringClass();
        A columnAnnotation = AnnotatedElementUtils.getMergedAnnotation(method, annotationClass);
        if (columnAnnotation == null) {
            Field field = ReflectionUtils.findField(dClass, fieldName);
            if (field != null) {
                columnAnnotation = AnnotatedElementUtils.getMergedAnnotation(field, annotationClass);
            }
        }
        return columnAnnotation;
    }
}
