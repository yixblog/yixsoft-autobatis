package cn.yixblog.support.mybatis.test.mappers;

import cn.yixblog.support.mybatis.autosql.annotations.AdvanceSelect;
import cn.yixblog.support.mybatis.autosql.annotations.AutoMapper;
import cn.yixblog.support.mybatis.autosql.annotations.AutoSql;
import cn.yixblog.support.mybatis.autosql.annotations.SqlType;
import cn.yixblog.support.mybatis.paginator.annotations.CountRef;
import cn.yixblog.support.mybatis.paginator.annotations.CountSqlTpl;
import com.alibaba.fastjson.JSONObject;
import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;

/**
 * Created by yixian on 2015-08-31.
 */
@AutoMapper(tablename = "sys_log", pkName = "id")
public interface BasicLogMapper {
    @AutoSql(type = SqlType.SELECT)
    PageList<JSONObject> list(JSONObject params, PageBounds pageBounds);

    @AutoSql(type = SqlType.SELECT)
    @CountRef(".count")
    PageList<JSONObject> pageListOne(JSONObject params, PageBounds pageBounds);

    @AutoSql(type = SqlType.SELECT)
    @CountSqlTpl("select count(1) from sys_log where id=#{userid}")
    PageList<JSONObject> pageListFake(JSONObject params, PageBounds pageBounds);

    @AutoSql(type = SqlType.SELECT)
    JSONObject findOne(String pkid);

    @AutoSql(type = SqlType.SELECT)
    @AdvanceSelect(excludeColumns = {"content"}, addonWhereClause = "content='222'")
    JSONObject findOneShort(String pkid);

    @AutoSql(type = SqlType.COUNT)
    Integer count(JSONObject params);

    @AutoSql(type = SqlType.INSERT)
    void save(JSONObject item);

    @AutoSql(type = SqlType.UPDATE)
    void update(JSONObject item);

    @AutoSql(type = SqlType.DELETE)
    void delete(String pkid);
}
