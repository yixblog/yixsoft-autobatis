package cn.yixblog.support.mybatis.test.mappers;

import cn.yixblog.support.mybatis.autosql.annotations.AutoMapper;
import cn.yixblog.support.mybatis.autosql.annotations.AutoSql;
import cn.yixblog.support.mybatis.autosql.annotations.SqlType;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * Created by yixian on 2015-08-31.
 */
@AutoMapper(tablename = "sys_log")
public interface BasicLogMapper {
    @AutoSql(type = SqlType.SELECT)
    List<JSONObject> list();

    @AutoSql(type = SqlType.FIND_ONE)
    JSONObject findOne(String pkid);
}
