package cn.yixblog.support.mybatis.test.mappers;

import cn.yixblog.support.mybatis.autosql.annotations.SqlType;
import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import cn.yixblog.support.mybatis.autosql.annotations.AdvanceSelect;
import cn.yixblog.support.mybatis.autosql.annotations.AutoMapper;
import cn.yixblog.support.mybatis.autosql.annotations.AutoSql;
import cn.yixblog.support.mybatis.test.ExampleAnotherDAO;
import cn.yixblog.support.mybatis.test.ExampleDAO;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * Create by davep at 2020-02-25 2:40
 */
@AutoMapper(tablename = "example", pkName = "entity_id")
public interface ExampleEntityMapper {
    @AutoSql(type = SqlType.SELECT)
    PageList<ExampleDAO> pageList(Map<String, Object> params, PageBounds pageBounds);

    @AdvanceSelect(addonWhereClause = "is_valid=1")
    ExampleDAO findOne(@Param("entity_id") String id);

    @AutoSql(type = SqlType.INSERT)
    void save(ExampleDAO entity);

    @AutoSql(type = SqlType.UPDATE)
    void update(ExampleDAO entity);

    @AutoSql(type = SqlType.INSERT)
    void save(ExampleAnotherDAO another);
}
