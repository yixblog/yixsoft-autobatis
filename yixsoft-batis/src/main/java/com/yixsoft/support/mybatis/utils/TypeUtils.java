package com.yixsoft.support.mybatis.utils;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;

public class TypeUtils {
    public static boolean isSingleType(Class<?> type) {
        if (type.isArray() || type.isEnum() || type.isPrimitive()) {
            return true;
        }
        Class<?>[] elementTypes = {Number.class, Boolean.class, Character.class, Date.class, Instant.class, CharSequence.class, Iterable.class};
        return Arrays.stream(elementTypes).anyMatch(eType -> eType.isAssignableFrom(type));
    }
}
