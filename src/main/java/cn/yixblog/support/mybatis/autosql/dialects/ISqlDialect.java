package cn.yixblog.support.mybatis.autosql.dialects;

import java.util.List;

/**
 * auto sql generator
 * Created by yixian on 2015-08-27.
 */
public interface ISqlDialect {
    String getName();

    List<ColumnInfo> selectTableColumns(String tableName);


}
