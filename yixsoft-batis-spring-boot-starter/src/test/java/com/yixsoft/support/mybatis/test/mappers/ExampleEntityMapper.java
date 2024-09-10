package com.yixsoft.support.mybatis.test.mappers;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.yixsoft.support.mybatis.autosql.annotations.AdvanceSelect;
import com.yixsoft.support.mybatis.autosql.annotations.AutoMapper;
import com.yixsoft.support.mybatis.autosql.annotations.AutoSql;
import com.yixsoft.support.mybatis.autosql.annotations.SqlType;
import com.yixsoft.support.mybatis.test.ExampleAnotherDAO;
import com.yixsoft.support.mybatis.test.ExampleDAO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.lang.constant.Constable;
import java.util.List;
import java.util.Map;

/**
 * Create by davep at 2020-02-25 2:40
 */
@AutoMapper(tablename = "example", pkName = "entity_id")
public interface ExampleEntityMapper {
    @AutoSql(SqlType.SELECT)
    PageList<ExampleDAO> pageList(Map<String, Object> params, PageBounds pageBounds);

    @AutoSql(SqlType.SELECT)
    PageList<ExampleDAO> pageList2(ExampleDAO params, PageBounds pageBounds);

    @AdvanceSelect(addonWhereClause = "is_valid=1")
    ExampleDAO findOne(@Param("entity_id") String id);

    @AutoSql(SqlType.INSERT)
    void save(ExampleDAO entity);

    @AutoSql(SqlType.UPDATE)
    void update(ExampleDAO entity);

    @AutoSql(SqlType.INSERT)
    void save(ExampleAnotherDAO another);

    @AdvanceSelect(rowNumberColumns = "rn",rowNumberOvers = "order by create_time")
    List<ExampleDAO> rnList(Map<String,Object> args, RowBounds rowBounds);
}
