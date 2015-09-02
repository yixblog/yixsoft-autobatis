package cn.yixblog.support.mybatis.test.mappers;

import cn.yixblog.support.mybatis.autosql.annotations.AutoMapper;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * Created by yixian on 2015-08-31.
 */
@AutoMapper(tablename = "sys_log")
public interface BasicLogMapper {
    List<JSONObject> list();

    JSONObject findOne();
}
