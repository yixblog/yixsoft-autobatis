package com.yixsoft.support.mybatis.typehandlers;

import com.yixsoft.support.mybatis.support.typedef.ClassFieldsDescription;
import com.yixsoft.support.mybatis.support.typedef.FieldDescription;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.MyBatisSystemException;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class YixBatisEnumTypeHandler<E extends Enum<E>> extends BaseTypeHandler<E> {
    private final Class<E> enumClassType;
    private final FieldDescription valueDescription;
    private final Class<?> valueType;

    public YixBatisEnumTypeHandler(Class<E> enumClassType) {
        if (enumClassType == null) {
            throw new IllegalArgumentException("Enum type shall not be null");
        }
        this.enumClassType = enumClassType;
        ClassFieldsDescription<E> desc = new ClassFieldsDescription<>(enumClassType);
        valueDescription = desc.findAnnotated(EnumValue.class)
                .orElseGet(() -> desc.findField("name")
                        //shall never happen
                        .orElseThrow(() -> new IllegalArgumentException("Enum " + enumClassType + " not annotated by enum value and has no name() method")));
        valueType = valueDescription.getFieldType();
    }

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, E e, JdbcType jdbcType) throws SQLException {
        if (jdbcType == null) {
            preparedStatement.setObject(i, this.getValue(e));
        } else {
            preparedStatement.setObject(i, this.getValue(e), jdbcType.TYPE_CODE);
        }
    }

    @Override
    public E getNullableResult(ResultSet resultSet, String s) throws SQLException {
        Object value = resultSet.getObject(s, this.valueType);
        if (value == null && resultSet.wasNull()) {
            return null;
        }
        return readValue(value);
    }

    @Override
    public E getNullableResult(ResultSet resultSet, int i) throws SQLException {
        Object value = resultSet.getObject(i, this.valueType);
        if (value == null && resultSet.wasNull()) {
            return null;
        }
        return readValue(value);
    }

    @Override
    public E getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        Object value = callableStatement.getObject(i, this.valueType);
        if (value == null && callableStatement.wasNull()) {
            return null;
        }
        return readValue(value);
    }

    private E readValue(Object dbValue) {
        return Arrays.stream(this.enumClassType.getEnumConstants())
                .filter(e -> equalsValue(getValue(e), dbValue))
                .findFirst().orElse(null);
    }

    protected boolean equalsValue(Object value1, Object value2) {
        if (value1 instanceof Number && value2 instanceof Number) {
            String value1Str = value1.toString();
            String value2Str = value2.toString();
            if (new BigDecimal(value1Str).compareTo(new BigDecimal(value2Str)) == 0) {
                return true;
            }
        }
        return Objects.equals(value1, value2);
    }

    private Object getValue(Object instance) {
        try {
            return this.valueDescription.readField(instance);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new MyBatisSystemException(e);
        }
    }
}
