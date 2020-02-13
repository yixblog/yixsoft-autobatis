package com.yixsoft.support.mybatis.dialects.oracle;

import com.yixsoft.support.mybatis.autosql.dialects.ColumnInfo;
import com.yixsoft.support.mybatis.autosql.dialects.ISqlDialect;
import com.yixsoft.support.mybatis.autosql.dialects.SupportsDatabase;
import com.yixsoft.support.mybatis.autosql.dialects.exceptions.AutoSqlException;
import com.yixsoft.support.mybatis.dialects.oracle.mappers.OracleColumnDAO;
import com.yixsoft.support.mybatis.dialects.oracle.mappers.OracleTableMapper;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.mapper.MapperFactoryBean;

import java.util.ArrayList;
import java.util.List;

/**
 * dialect impl for oracle
 * Created by yixian on 2015-09-05.
 */
@SupportsDatabase("oracle")
public class OracleDialect implements ISqlDialect {
    private OracleTableMapper tableMapper;

    @Override
    public void init(MapperFactoryBean parentFactory) {
        Configuration configuration = parentFactory.getSqlSession().getConfiguration();
        if (!configuration.hasMapper(OracleTableMapper.class)) {
            configuration.addMapper(OracleTableMapper.class);
        }
        MapperFactoryBean<OracleTableMapper> factory = new MapperFactoryBean<>();
        factory.setAddToConfig(parentFactory.isAddToConfig());
        factory.setMapperInterface(OracleTableMapper.class);
        factory.setSqlSessionFactory(parentFactory.getSqlSessionFactory());
        factory.setSqlSessionTemplate(parentFactory.getSqlSessionTemplate());
        try {
            tableMapper = factory.getObject();
        } catch (Exception e) {
            throw new AutoSqlException("Failed to init table mapper in mysql dialect");
        }
    }

    @Override
    public List<ColumnInfo> selectTableColumns(String tableName) {
        List<OracleColumnDAO> tableColumns = tableMapper.listOracleTableColumns(tableName);
        List<ColumnInfo> columns = new ArrayList<>();
        for (OracleColumnDAO col : tableColumns) {
            ColumnInfo info = new ColumnInfo();
            info.setAllowNull(BooleanUtils.toBoolean(col.getNullable()));
            info.setColumn(col.getColumnName());
            info.setJdbcType(col.getDataType());
            columns.add(info);
        }
        return columns;
    }

    @Override
    public String escapeKeyword(String name) {
        return "\"" + name + "\"";
    }

}
