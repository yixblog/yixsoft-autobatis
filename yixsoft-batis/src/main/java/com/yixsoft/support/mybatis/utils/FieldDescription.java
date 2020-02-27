package com.yixsoft.support.mybatis.utils;

import com.google.common.base.CaseFormat;
import com.yixsoft.support.mybatis.autosql.annotations.Column;
import com.yixsoft.support.mybatis.autosql.dialects.exceptions.AutoSqlException;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Locale.ENGLISH;

/**
 * Create by davep at 2020-02-27 16:53
 */
public class FieldDescription implements Comparable<FieldDescription> {
    private final int matchRank;
    private final Method method;
    private final String fieldName;
    private final String readMethodName;
    private final List<String> possibleNames;
    private String matchingColumn;

    public FieldDescription(Method method) {
        this.method = method;
        this.readMethodName = method.getName();
        this.fieldName = capitalize(readMethodName.replaceFirst("(get|is)", ""));
        Column columnAnnotation = findFieldAnnotation(method, fieldName);

        String annotationName = Optional.ofNullable(columnAnnotation).map(Column::value).orElse(null);
        if (annotationName != null) {
            this.matchRank = 0;
            this.possibleNames = new ArrayList<>(Collections.singletonList(annotationName));
        } else {
            this.possibleNames = Arrays.stream(CaseFormat.values())
                    .flatMap(sFormat -> Arrays.stream(CaseFormat.values())
                            .map(tFormat -> sFormat.to(tFormat, fieldName)))
                    .distinct()
                    .collect(Collectors.toList());
            this.matchRank = 1;
        }
    }

    public Object readField(Object instance) {
        try {
            return method.invoke(instance);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new AutoSqlException("Failed to invoke getter for " + getFieldName() + " in " + instance.getClass());
        }
    }

    private static String capitalize(String name) {
        return name.substring(0, 1).toLowerCase(ENGLISH) + name.substring(1);
    }

    private static Column findFieldAnnotation(Method method, String fieldName) {
        Class<?> dClass = method.getDeclaringClass();
        Column columnAnnotation = AnnotatedElementUtils.getMergedAnnotation(method, Column.class);
        if (columnAnnotation == null) {
            Field field = ReflectionUtils.findField(dClass, fieldName);
            if (field != null) {
                columnAnnotation = AnnotatedElementUtils.getMergedAnnotation(field, Column.class);
            }
        }
        return columnAnnotation;
    }

    public int getMatchRank() {
        return matchRank;
    }

    public Method getMethod() {
        return method;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getReadMethodName() {
        return readMethodName;
    }

    public List<String> getPossibleNames() {
        return possibleNames;
    }

    public boolean matchName(String searchColumnName) {
        boolean match = possibleNames.stream().anyMatch(name -> name.equals(searchColumnName));
        if (match) {
            matchingColumn = searchColumnName;
        }
        return match;
    }

    @Override
    public int compareTo(FieldDescription o) {
        return Integer.compare(this.matchRank, o.matchRank);
    }

    public String getMatchingColumn() {
        return matchingColumn;
    }
}
