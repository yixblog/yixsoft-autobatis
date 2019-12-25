package com.yixsoft.support.mybatis.dialects.mysql.mappers;

import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * describe mysql table columns
 * Created by yixian on 2015-09-01.
 */
public interface DescMySqlTableMapper {
    @Select("select column_name,data_type,is_nullable from information_schema.columns where table_name=#{tableName} and table_schema=database()")
    List<JSONObject> descTable(@Param("tableName") String tableName);
}
