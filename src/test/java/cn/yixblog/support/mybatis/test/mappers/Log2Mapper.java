package cn.yixblog.support.mybatis.test.mappers;

import cn.yixblog.support.mybatis.autosql.annotations.AutoMapper;
import cn.yixblog.support.mybatis.autosql.annotations.AutoSql;
import cn.yixblog.support.mybatis.autosql.annotations.SqlType;
import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;

/**
 * Created by davep on 2016-07-29.
 */
@AutoMapper(tablename = "test_log2", pkName = "pkid", keyGenerator = Jdbc3KeyGenerator.class)
public interface Log2Mapper {
    @AutoSql(type = SqlType.INSERT)
    void save(JSONObject item);
}
