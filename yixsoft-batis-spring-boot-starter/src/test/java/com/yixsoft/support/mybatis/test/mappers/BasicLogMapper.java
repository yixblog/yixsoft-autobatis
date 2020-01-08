package com.yixsoft.support.mybatis.test.mappers;

import com.yixsoft.support.mybatis.autosql.annotations.*;
import com.yixsoft.support.mybatis.paginator.annotations.CountRef;
import com.yixsoft.support.mybatis.paginator.annotations.CountSqlTpl;
import com.alibaba.fastjson.JSONObject;
import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.yixsoft.support.mybatis.test.LogEntity;

/**
 * Created by yixian on 2015-08-31.
 */
@AutoMapper(tablename = "sys_log", pkName = "id")
public interface BasicLogMapper {
    @AutoSql(value = SqlType.SELECT)
    PageList<JSONObject> list(JSONObject params, PageBounds pageBounds);

    @AutoSql(value = SqlType.SELECT)
    @CountRef(".count")
    PageList<JSONObject> pageListOne(JSONObject params, PageBounds pageBounds);

    @AutoSql(value = SqlType.SELECT)
    @CountSqlTpl("select count(1) from sys_log where id=#{userid}")
    PageList<JSONObject> pageListFake(JSONObject params, PageBounds pageBounds);

    @AutoSql(value = SqlType.SELECT)
    JSONObject findOne(String pkid);

    @AdvanceSelect(excludeColumns = {"content"}, addonWhereClause = "content='333'")
    JSONObject findOneShort(String pkid);

    @AutoSql(value = SqlType.COUNT)
    Integer count(JSONObject params);

    @AutoSql(value = SqlType.INSERT)
    void save(JSONObject item);

    @AutoSql(value = SqlType.UPDATE)
    void update(JSONObject item);

    @AutoSql(value = SqlType.DELETE)
    void delete(String pkid);

    @StaticUpdate("content='333'")
    void updateContentTo333(String pkid);

    @AutoSql(SqlType.INSERT)
    void saveEntity(LogEntity log);
}
