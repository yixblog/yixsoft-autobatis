package com.yixsoft.support.mybatis.support.typedef;

import com.yixsoft.support.mybatis.autosql.dialects.exceptions.AutoSqlException;
import org.springframework.util.Assert;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Create by davep at 2020-02-27 16:35
 */
public class ClassFieldsDescription<T> {
    private final Class<T> type;
    private final List<FieldDescription> fields;

    public ClassFieldsDescription(Class<T> type) {
        this.type = type;
        this.fields = describeFields(type);
    }

    public Map<String, Object> convertToMap(Object instance, boolean ignoreNullValue) {
        Assert.notNull(instance, "Instance should not be null");
        return fields.stream()
                .map(field -> {
                    try {
                        return new KeyValuePair<>(field.getFieldName(), field.readField(instance));
                    } catch (InvocationTargetException | IllegalAccessException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .filter(pair -> !ignoreNullValue || pair.valueNotNull())
                .collect(HashMap::new, (map, pair) -> map.put(pair.getKey(), pair.getValue()), HashMap::putAll);
    }

    public Class<T> getType() {
        return type;
    }

    public List<FieldDescription> getFields() {
        return fields;
    }

    public Optional<FieldDescription> findAnnotated(Class<? extends Annotation> annotationClass) {
        return fields.stream().filter(field -> field.findAnnotation(annotationClass) != null).findFirst();
    }

    public List<FieldDescription> listAnnotated(Class<? extends Annotation> annotationClass) {
        return fields.stream().filter(field -> field.findAnnotation(annotationClass) != null).collect(Collectors.toList());
    }

    public Optional<FieldDescription> findField(String fieldName) {
        return fields.stream().filter(field -> field.getFieldName().equals(fieldName)).findFirst();
    }

    private List<FieldDescription> describeFields(Class<?> cls) {
        try {
            Field[] declaredFields = cls.getDeclaredFields();
            Map<String, FieldDescription> fieldDescriptionMap = Arrays.stream(declaredFields)
                    .filter(field -> !Modifier.isStatic(field.getModifiers()))
                    .map(DirectFieldDescription::new).collect(HashMap::new, (map, desc) -> map.put(desc.getFieldName(), desc), Map::putAll);
            Map<String, FieldDescription> getterFields = Arrays.stream(Introspector.getBeanInfo(cls).getMethodDescriptors())
                    .map(MethodDescriptor::getMethod)
                    .filter(method -> isReaderMethod(method, declaredFields))
                    .map(GetterMethodFieldDescription::new)
                    .collect(HashMap::new, (map, desc) -> map.put(desc.getFieldName(), desc), Map::putAll);
            fieldDescriptionMap.putAll(getterFields);
            return new ArrayList<>(fieldDescriptionMap.values());
        } catch (IntrospectionException e) {
            throw new AutoSqlException("Failed to describe class " + cls.getName(), e);
        }
    }

    private static boolean isReaderMethod(Method method, Field[] availableFields) {
        if (method.getParameterCount() > 0) {
            return false;
        }
        if (Modifier.isStatic(method.getModifiers())) {
            return false;
        }
        String methodName = method.getName();
        if ("getClass".equals(methodName) || "getDeclaringClass".equals(methodName)) {
            return false;
        }
        if (methodName.matches("^get[A-Z]\\w*")) {
            return true;
        }
        Class<?> returnType = method.getReturnType();
        if (methodName.matches("^is[A-Z]\\w*") && returnType == boolean.class) {
            return true;
        }
        if (method.getDeclaringClass().isEnum()|| Enum.class.isAssignableFrom(method.getDeclaringClass())) {
            return true;
        }
        return Arrays.stream(availableFields).anyMatch(field -> methodName.equals(field.getName()));
    }


    public static class KeyValuePair<V> {
        private final String key;
        private final V value;

        public KeyValuePair(String key, V value) {
            this.key = key;
            this.value = value;
        }

        public boolean valueNotNull() {
            return value != null;
        }

        public String getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }
    }
}
