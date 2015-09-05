package cn.yixblog.support.mybatis.autosql.dialects.oracle.mappers;

import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * mapper for oracle table
 * Created by yixian on 2015-09-05.
 */
public interface OracleTableMapper {
    @Select("SELECT COLUMN_NAME,DATA_TYPE,NULLABLE FROM USER_TAB_COLUMNS WHERE TABLE_NAME=#{tableName}")
    List<JSONObject> listOracleTableColumns(String tableName);
}
