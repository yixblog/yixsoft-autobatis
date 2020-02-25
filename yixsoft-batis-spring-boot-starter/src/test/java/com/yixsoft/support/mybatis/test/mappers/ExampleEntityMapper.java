package com.yixsoft.support.mybatis.test.mappers;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.yixsoft.support.mybatis.autosql.annotations.AdvanceSelect;
import com.yixsoft.support.mybatis.autosql.annotations.AutoMapper;
import com.yixsoft.support.mybatis.autosql.annotations.AutoSql;
import com.yixsoft.support.mybatis.autosql.annotations.SqlType;
import com.yixsoft.support.mybatis.test.ExampleDAO;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * Create by davep at 2020-02-25 2:40
 */
@AutoMapper(tablename = "example", pkName = "entity_id")
public interface ExampleEntityMapper {
    @AutoSql(SqlType.SELECT)
    PageList<ExampleDAO> pageList(Map<String, Object> params, PageBounds pageBounds);

    @AdvanceSelect(addonWhereClause = "is_valid=1")
    ExampleDAO findOne(@Param("entity_id") String id);

    @AutoSql(SqlType.INSERT)
    void save(ExampleDAO entity);
}
