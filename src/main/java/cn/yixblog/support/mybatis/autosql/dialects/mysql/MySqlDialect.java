package cn.yixblog.support.mybatis.autosql.dialects.mysql;

import cn.yixblog.support.mybatis.autosql.dialects.ColumnInfo;
import cn.yixblog.support.mybatis.autosql.dialects.ISqlDialect;
import cn.yixblog.support.mybatis.autosql.dialects.mysql.mappers.DescMySqlTableMapper;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * dialect for mysql
 * Created by yixian on 2015-09-01.
 */
@Component
public class MySqlDialect implements ISqlDialect {
    @Autowired(required = false)
    private DescMySqlTableMapper tableMapper;

    @Override
    public String getName() {
        return "mysql";
    }

    @Override
    public List<ColumnInfo> selectTableColumns(String tableName) {
        List<JSONObject> columns = tableMapper.descTable(tableName);
        List<ColumnInfo> infos = new ArrayList<>();
        for (JSONObject column : columns) {
            ColumnInfo info = new ColumnInfo();
            info.setColumn(column.getString("column_name"));
            info.setJdbcType(column.getString("data_type"));
            info.setAllowNull("yes".equalsIgnoreCase(column.getString("is_nullable")));
            infos.add(info);
        }
        return infos;
    }
}
