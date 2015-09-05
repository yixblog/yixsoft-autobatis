package cn.yixblog.support.mybatis.autosql.configuration.support.dialect;

import cn.yixblog.support.mybatis.autosql.configuration.support.ISqlDialectManager;
import cn.yixblog.support.mybatis.autosql.dialects.ISqlDialect;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * dialect manager impl
 * Created by yixian on 2015-09-02.
 */
@Component
public class SqlDialectManager implements ISqlDialectManager {
    private Map<String, ISqlDialect> dialectMap = new HashMap<>();
    @Value("${mybatis.autosql.default-dialect}")
    private String defaultDialect;

    @Resource
    public void setDialects(ISqlDialect[] dialects) {
        if (dialects != null) {
            for (ISqlDialect dialect : dialects) {
                dialectMap.put(dialect.getName().toLowerCase(), dialect);
            }
        }
    }

    @Override
    public ISqlDialect getDialect(String name) {
        name = StringUtils.isEmpty(name) ? defaultDialect : name;
        return dialectMap.get(name.toLowerCase());
    }
}
