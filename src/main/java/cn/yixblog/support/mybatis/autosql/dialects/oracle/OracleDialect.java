package cn.yixblog.support.mybatis.autosql.dialects.oracle;

import cn.yixblog.support.mybatis.autosql.dialects.ColumnInfo;
import cn.yixblog.support.mybatis.autosql.dialects.ISqlDialect;
import cn.yixblog.support.mybatis.autosql.dialects.oracle.mappers.OracleTableMapper;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * dialect impl for oracle
 * Created by yixian on 2015-09-05.
 */
@Component
public class OracleDialect implements ISqlDialect {
    @Autowired(required = false)
    private OracleTableMapper oracleTableMapper;

    @Override
    public String getName() {
        return "oracle";
    }

    @Override
    public List<ColumnInfo> selectTableColumns(String tableName) {
        List<JSONObject> tableColumns = oracleTableMapper.listOracleTableColumns(tableName);
        List<ColumnInfo> columns = new ArrayList<>();
        for (JSONObject col : tableColumns) {
            ColumnInfo info = new ColumnInfo();
            info.setAllowNull(BooleanUtils.toBoolean(col.getString("nullable")));
            info.setColumn(col.getString("column_name"));
            info.setJdbcType(col.getString("data_type"));
            columns.add(info);
        }
        return columns;
    }

    @Override
    public String escapeKeyword(String name) {
        return "\"" + name + "\"";
    }
}
