package cn.yixblog.support.mybatis.test.mappers;

import cn.yixblog.support.mybatis.autosql.annotations.AutoSql;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * Created by yixian on 2015-08-31.
 */
@AutoSql(tablename = "sys_log")
public interface BasicLogMapper {
    List<JSONObject> list();

    JSONObject findOne();
}
