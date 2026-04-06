package com.yixsoft.support.mybatis.dialects.pg;

import com.yixsoft.support.mybatis.autosql.dialects.ColumnInfo;
import com.yixsoft.support.mybatis.autosql.dialects.ISqlDialect;
import com.yixsoft.support.mybatis.autosql.dialects.SupportsDatabase;
import com.yixsoft.support.mybatis.autosql.dialects.entities.RowNumberConfig;
import com.yixsoft.support.mybatis.autosql.dialects.exceptions.AutoSqlException;
import com.yixsoft.support.mybatis.dialects.pg.mappers.PostgreSqlTableMapper;
import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.mapper.MapperFactoryBean;

import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.List;

/**
 * PostgreSQL数据库方言实现
 * 支持PostgreSQL数据库的SQL自动生成功能
 */
@SupportsDatabase({"postgresql", "postgres"})
public class PostgreSqlDialect implements ISqlDialect {

    private PostgreSqlTableMapper tableMapper;

    @Override
    public void init(MapperFactoryBean parentFactory) {
        Configuration configuration = parentFactory.getSqlSession().getConfiguration();
        if (configuration.hasMapper(PostgreSqlTableMapper.class)) {
            tableMapper = configuration.getMapper(PostgreSqlTableMapper.class, parentFactory.getSqlSession());
            return;
        } else {
            configuration.addMapper(PostgreSqlTableMapper.class);
        }
        MapperFactoryBean<PostgreSqlTableMapper> factory = new MapperFactoryBean<>();
        factory.setAddToConfig(parentFactory.isAddToConfig());
        factory.setMapperInterface(PostgreSqlTableMapper.class);
        factory.setSqlSessionFactory(parentFactory.getSqlSessionFactory());
        factory.setSqlSessionTemplate(parentFactory.getSqlSessionTemplate());
        try {
            tableMapper = factory.getObject();
        } catch (Exception e) {
            throw new AutoSqlException("Failed to init table mapper in PostgreSQL dialect", e);
        }
    }

    @Override
    public List<ColumnInfo> selectTableColumns(String tableName) {
        List<PostgreSqlColumnDAO> columns = tableMapper.selectColumns(tableName);
        List<ColumnInfo> infos = new ArrayList<>();
        for (PostgreSqlColumnDAO column : columns) {
            ColumnInfo info = new ColumnInfo();
            info.setColumn(column.getColumnName());
            info.setJdbcType(mapPostgreSqlTypeToJdbcType(column.getDataType()));
            info.setAllowNull("YES".equalsIgnoreCase(column.getIsNullable()));
            infos.add(info);
        }
        return infos;
    }

    @Override
    public String escapeKeyword(String name) {
        return "\"" + name + "\"";
    }

    @Override
    public String rowNumberFragment(RowNumberConfig rowNumberConfig) {
        return "row_number() over (%s) as %s".formatted(rowNumberConfig.over(), rowNumberConfig.asColumn());
    }

    /**
     * 将PostgreSQL数据类型映射到JDBC类型
     *
     * @param postgreSqlType PostgreSQL数据类型名称
     * @return JDBC类型
     */
    private JDBCType mapPostgreSqlTypeToJdbcType(String postgreSqlType) {
        if (postgreSqlType == null) {
            return JDBCType.VARCHAR;
        }

        String type = postgreSqlType.toLowerCase();

        // 整数类型
        if ("integer".equals(type) || "int".equals(type) || "int4".equals(type)) {
            return JDBCType.INTEGER;
        }
        if ("bigint".equals(type) || "int8".equals(type)) {
            return JDBCType.BIGINT;
        }
        if ("smallint".equals(type) || "int2".equals(type)) {
            return JDBCType.SMALLINT;
        }

        // 字符串类型
        if ("character varying".equals(type) || "varchar".equals(type)) {
            return JDBCType.VARCHAR;
        }
        if ("text".equals(type)) {
            return JDBCType.LONGVARCHAR;
        }
        if ("character".equals(type) || "char".equals(type) || "bpchar".equals(type)) {
            return JDBCType.CHAR;
        }

        // 布尔类型
        if ("boolean".equals(type) || "bool".equals(type)) {
            return JDBCType.BOOLEAN;
        }

        // 时间类型
        if ("timestamp without time zone".equals(type) || "timestamp".equals(type)) {
            return JDBCType.TIMESTAMP;
        }
        if ("timestamp with time zone".equals(type) || "timestamptz".equals(type)) {
            return JDBCType.TIMESTAMP_WITH_TIMEZONE;
        }
        if ("date".equals(type)) {
            return JDBCType.DATE;
        }
        if ("time without time zone".equals(type) || "time".equals(type)) {
            return JDBCType.TIME;
        }
        if ("time with time zone".equals(type) || "timetz".equals(type)) {
            return JDBCType.TIME_WITH_TIMEZONE;
        }

        // 数值类型
        if ("numeric".equals(type) || "decimal".equals(type)) {
            return JDBCType.NUMERIC;
        }
        if ("real".equals(type) || "float4".equals(type)) {
            return JDBCType.FLOAT;
        }
        if ("double precision".equals(type) || "float8".equals(type)) {
            return JDBCType.DOUBLE;
        }

        // 二进制类型
        if ("bytea".equals(type)) {
            return JDBCType.BINARY;
        }

        // PostgreSQL特殊类型（映射为VARCHAR，用户可自定义TypeHandler）
        if ("json".equals(type) || "jsonb".equals(type) || "uuid".equals(type) ||
            "xml".equals(type) || "money".equals(type)) {
            return JDBCType.VARCHAR;
        }

        // 数组类型（映射为VARCHAR）
        if (type.endsWith("[]")) {
            return JDBCType.VARCHAR;
        }

        // 默认映射为VARCHAR
        return JDBCType.VARCHAR;
    }
}