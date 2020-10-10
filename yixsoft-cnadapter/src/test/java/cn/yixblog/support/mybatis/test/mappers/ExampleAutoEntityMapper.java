package cn.yixblog.support.mybatis.test.mappers;

import cn.yixblog.support.mybatis.autosql.annotations.AdvanceSelect;
import cn.yixblog.support.mybatis.autosql.annotations.AutoMapper;
import cn.yixblog.support.mybatis.autosql.annotations.AutoSql;
import cn.yixblog.support.mybatis.autosql.annotations.SqlType;
import cn.yixblog.support.mybatis.test.ExampleAnotherDAO;
import cn.yixblog.support.mybatis.test.ExampleAutoIdDAO;
import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;

import java.util.Map;

/**
 * Create by davep at 2020-02-25 2:40
 */
@AutoMapper(tablename = "example_auto", pkName = "entity_id", keyGenerator = Jdbc3KeyGenerator.class)
public interface ExampleAutoEntityMapper {
    @AutoSql(type = SqlType.SELECT)
    PageList<ExampleAutoIdDAO> pageList(Map<String, Object> params, PageBounds pageBounds);

    @AdvanceSelect(addonWhereClause = "is_valid=1")
    ExampleAutoIdDAO findOne(@Param("entity_id") String id);

    @AutoSql(type = SqlType.INSERT)
    void save(ExampleAutoIdDAO entity);

    @AutoSql(type = SqlType.UPDATE)
    void update(ExampleAutoIdDAO entity);

    @AutoSql(type = SqlType.INSERT)
    void save(ExampleAnotherDAO another);
}
