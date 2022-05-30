package com.yixsoft.support.mybatis.utils;

import com.yixsoft.support.mybatis.autosql.dialects.exceptions.AutoSqlException;
import org.springframework.util.Assert;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

    private static List<FieldDescription> describeFields(Class<?> cls) {
        try {
            Field[] declaredFields = cls.getDeclaredFields();
            return Arrays.stream(Introspector.getBeanInfo(cls).getMethodDescriptors())
                    .map(MethodDescriptor::getMethod)
                    .filter(method -> isReaderMethod(method, declaredFields))
                    .map(FieldDescription::new)
                    .collect(Collectors.toList());
        } catch (IntrospectionException e) {
            throw new AutoSqlException("Failed to describe class " + cls.getName(), e);
        }
    }

    private static boolean isReaderMethod(Method method, Field[] availableFields) {
        if (method.getParameterCount() > 0) {
            return false;
        }
        String methodName = method.getName();
        if (methodName.equals("getClass")) {
            return false;
        }
        if (methodName.matches("^get[A-Z]\\w*")) {
            return true;
        }
        Class<?> returnType = method.getReturnType();
        if (methodName.matches("^is[A-Z]\\w*") && returnType == boolean.class) {
            return true;
        }
        return Arrays.stream(availableFields).anyMatch(field -> methodName.equals(field.getName()));
    }

    public Map<String, FieldDescription> mapFields(Set<String> columnNames) {
        return columnNames.stream()
                .map(column -> new KeyValuePair<>(column, findMatchField(column)))
                .filter(KeyValuePair::valueNotNull)
                .collect(HashMap::new, (map, pair) -> map.put(pair.getKey(), pair.getValue()), HashMap::putAll);
    }

    public FieldDescription findMatchField(String name) {
        return fields.stream().filter(desc -> desc.matchName(name))
                .min(Comparator.comparing(FieldDescription::getMatchRank)).orElse(null);
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
