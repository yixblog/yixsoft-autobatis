package com.yixsoft.support.mybatis.test.mappers;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.yixsoft.support.mybatis.autosql.annotations.*;
import com.yixsoft.support.mybatis.paginator.annotations.CountRef;
import com.yixsoft.support.mybatis.paginator.annotations.CountSqlTpl;
import com.yixsoft.support.mybatis.test.LogEntity;

import java.util.Map;

/**
 * Created by yixian on 2015-08-31.
 */
@AutoMapper(tablename = "sys_log", pkName = "id")
public interface BasicLogMapper {
    @AutoSql(value = SqlType.SELECT)
    PageList<Map<String, Object>> list(Map<String, Object> params, PageBounds pageBounds);

    @AutoSql(value = SqlType.SELECT)
    @CountRef(".count")
    PageList<Map<String,Object>> pageListOne(Map<String,Object> params, PageBounds pageBounds);

    @AutoSql(value = SqlType.SELECT)
    @CountSqlTpl("select count(1) from sys_log where id=#{userid}")
    PageList<Map<String,Object>> pageListFake(Map<String,Object> params, PageBounds pageBounds);

    @AutoSql(value = SqlType.SELECT)
    Map<String,Object> findOne(String pkid);

    @AdvanceSelect(excludeColumns = {"content"}, addonWhereClause = "content='333'")
    Map<String,Object> findOneShort(String pkid);

    @AutoSql(value = SqlType.COUNT)
    Integer count(Map<String,Object> params);

    @AutoSql(value = SqlType.INSERT)
    void save(Map<String,Object> item);

    @AutoSql(value = SqlType.UPDATE)
    void update(Map<String,Object> item);

    @AutoSql(value = SqlType.DELETE)
    void delete(String pkid);

    @StaticUpdate("content='333'")
    void updateContentTo333(String pkid);

    @AutoSql(SqlType.INSERT)
    void saveEntity(LogEntity log);
}
