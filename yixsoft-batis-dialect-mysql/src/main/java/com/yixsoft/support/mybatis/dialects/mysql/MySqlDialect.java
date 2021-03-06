package com.yixsoft.support.mybatis.dialects.mysql;

import com.mysql.cj.MysqlType;
import com.yixsoft.support.mybatis.autosql.dialects.ColumnInfo;
import com.yixsoft.support.mybatis.autosql.dialects.ISqlDialect;
import com.yixsoft.support.mybatis.autosql.dialects.SupportsDatabase;
import com.yixsoft.support.mybatis.autosql.dialects.exceptions.AutoSqlException;
import com.yixsoft.support.mybatis.dialects.mysql.mappers.DescMySQLDAO;
import com.yixsoft.support.mybatis.dialects.mysql.mappers.DescMySqlTableMapper;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.mapper.MapperFactoryBean;

import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.List;

/**
 * dialect for mysql
 * Created by yixian on 2015-09-01.
 */
@SupportsDatabase({"mysql", "mariadb"})
public class MySqlDialect implements ISqlDialect {

    private DescMySqlTableMapper tableMapper;

    @Override
    public void init(MapperFactoryBean parentFactory) {
        Configuration configuration = parentFactory.getSqlSession().getConfiguration();
        if (configuration.hasMapper(DescMySqlTableMapper.class)) {
            tableMapper = configuration.getMapper(DescMySqlTableMapper.class, parentFactory.getSqlSession());
            return;
        } else {
            configuration.addMapper(DescMySqlTableMapper.class);
        }
        MapperFactoryBean<DescMySqlTableMapper> factory = new MapperFactoryBean<>();
        factory.setAddToConfig(parentFactory.isAddToConfig());
        factory.setMapperInterface(DescMySqlTableMapper.class);
        factory.setSqlSessionFactory(parentFactory.getSqlSessionFactory());
        factory.setSqlSessionTemplate(parentFactory.getSqlSessionTemplate());
        try {
            tableMapper = factory.getObject();
        } catch (Exception e) {
            throw new AutoSqlException("Failed to init table mapper in mysql dialect", e);
        }
    }

    @Override
    public List<ColumnInfo> selectTableColumns(String tableName) {
        List<DescMySQLDAO> columns = tableMapper.descTable(tableName);
        List<ColumnInfo> infos = new ArrayList<>();
        for (DescMySQLDAO column : columns) {
            ColumnInfo info = new ColumnInfo();
            info.setColumn(column.getColumnName());
            info.setJdbcType(JDBCType.valueOf(MysqlType.valueOf(column.getDataType().toUpperCase()).getJdbcType()));
            info.setAllowNull(BooleanUtils.toBoolean(column.getIsNullable()));
            infos.add(info);
        }
        return infos;
    }

    @Override
    public String escapeKeyword(String name) {
        return "`" + name + "`";
    }

}
