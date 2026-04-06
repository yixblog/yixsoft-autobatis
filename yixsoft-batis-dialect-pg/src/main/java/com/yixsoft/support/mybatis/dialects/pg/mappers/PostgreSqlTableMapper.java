package com.yixsoft.support.mybatis.dialects.pg.mappers;

import com.yixsoft.support.mybatis.dialects.pg.PostgreSqlColumnDAO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * PostgreSQL表结构查询Mapper
 * 使用information_schema查询表列信息
 */
public interface PostgreSqlTableMapper {

    /**
     * 查询表的列信息
     *
     * @param tableName 表名
     * @return 列信息列表
     */
    @Select("""
        SELECT 
            column_name,
            data_type,
            is_nullable,
            column_default,
            character_maximum_length,
            numeric_precision,
            numeric_scale,
            ordinal_position
        FROM information_schema.columns
        WHERE table_name = #{tableName}
        ORDER BY ordinal_position
        """)
    List<PostgreSqlColumnDAO> selectColumns(@Param("tableName") String tableName);
}