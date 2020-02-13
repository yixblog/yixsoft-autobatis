package com.yixsoft.support.mybatis.dialects.mysql.mappers;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * describe mysql table columns
 * Created by yixian on 2015-09-01.
 */
public interface DescMySqlTableMapper {
    @Select("select column_name,data_type,is_nullable from information_schema.columns where table_name=#{tableName} and table_schema=database()")
    @Results({
            @Result(id = true, column = "column_name", property = "columnName"),
            @Result(column = "data_type", property = "dataType"),
            @Result(column = "is_nullable", property = "isNullable")
    })
    List<DescMySQLDAO> descTable(@Param("tableName") String tableName);
}
