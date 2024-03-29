package com.yixsoft.support.mybatis.autosql.pk;

import com.yixsoft.support.mybatis.support.typedef.ClassFieldsDescription;
import com.yixsoft.support.mybatis.autosql.configuration.support.ColumnFieldInfo;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;

import java.sql.Statement;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class AbstractKeyProvider<K> implements IPrimaryKeyProvider {
    @Override
    public void processBefore(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
        String[] keyColumns = ms.getKeyColumns();
        if (parameter != null && keyColumns.length == 1) {
            final Configuration configuration = ms.getConfiguration();
            final MetaObject metaParam = configuration.newMetaObject(parameter);
            String paramName = keyColumns[0];
            if (!(parameter instanceof Map)) {
                paramName = Optional.ofNullable(ColumnFieldInfo.findMatchField(new ClassFieldsDescription<>(parameter.getClass()).getFields().stream().map(ColumnFieldInfo::new).collect(Collectors.toList()), paramName))
                        .map(ColumnFieldInfo::getFieldName)
                        .orElse(paramName);
                if (!metaParam.hasGetter(paramName)) {
                    throw new ExecutorException("No getter found for the keyProperty '" + paramName + "' in " + metaParam.getOriginalObject().getClass().getName() + ".");
                }
            }
            if (metaParam.getValue(paramName) == null) {
                setValue(metaParam, paramName, nextKey());
            }
        }
    }

    @Override
    public void processAfter(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
        //do nothing
//        System.out.println(parameter.getClass());
    }

    private void setValue(MetaObject metaParam, String property, K value) {
        if (metaParam.hasSetter(property)) {
            metaParam.setValue(property, value);
        } else {
            throw new ExecutorException("No setter found for the keyProperty '" + property + "' in " + metaParam.getOriginalObject().getClass().getName() + ".");
        }
    }

    protected abstract K nextKey();
}
