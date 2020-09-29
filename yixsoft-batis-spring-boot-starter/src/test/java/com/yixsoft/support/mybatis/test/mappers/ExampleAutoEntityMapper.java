package com.yixsoft.support.mybatis.test.mappers;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.yixsoft.support.mybatis.autosql.annotations.AdvanceSelect;
import com.yixsoft.support.mybatis.autosql.annotations.AutoMapper;
import com.yixsoft.support.mybatis.autosql.annotations.AutoSql;
import com.yixsoft.support.mybatis.autosql.annotations.SqlType;
import com.yixsoft.support.mybatis.test.ExampleAnotherDAO;
import com.yixsoft.support.mybatis.test.ExampleAutoIdDAO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;

import java.util.Map;

/**
 * Create by davep at 2020-02-25 2:40
 */
@AutoMapper(tablename = "example_auto", pkName = "entity_id", keyGenerator = Jdbc3KeyGenerator.class)
public interface ExampleAutoEntityMapper {
    @AutoSql(SqlType.SELECT)
    PageList<ExampleAutoIdDAO> pageList(Map<String, Object> params, PageBounds pageBounds);

    @AdvanceSelect(addonWhereClause = "is_valid=1")
    ExampleAutoIdDAO findOne(@Param("entity_id") String id);

    @AutoSql(SqlType.INSERT)
    void save(ExampleAutoIdDAO entity);

    @AutoSql(SqlType.UPDATE)
    void update(ExampleAutoIdDAO entity);

    @AutoSql(SqlType.INSERT)
    void save(ExampleAnotherDAO another);
}
