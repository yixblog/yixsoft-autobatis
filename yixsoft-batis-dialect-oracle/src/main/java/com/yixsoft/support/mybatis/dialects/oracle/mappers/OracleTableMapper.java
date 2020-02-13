package com.yixsoft.support.mybatis.dialects.oracle.mappers;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * mapper for oracle table
 * Created by yixian on 2015-09-05.
 */
public interface OracleTableMapper {
    @Select("SELECT COLUMN_NAME,DATA_TYPE,NULLABLE FROM USER_TAB_COLUMNS WHERE TABLE_NAME=#{tableName}")
    @Results({
            @Result(id = true, column = "COLUMN_NAME", property = "columnName"),
            @Result(column = "DATA_TYPE", property = "dataType"),
            @Result(column = "NULLABLE", property = "nullable")
    })
    List<OracleColumnDAO> listOracleTableColumns(@Param("tableName") String tableName);
}
