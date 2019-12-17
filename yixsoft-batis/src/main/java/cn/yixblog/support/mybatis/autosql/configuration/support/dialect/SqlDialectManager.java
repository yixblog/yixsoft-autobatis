package cn.yixblog.support.mybatis.autosql.configuration.support.dialect;

import cn.yixblog.support.mybatis.autosql.configuration.support.spring.ApplicationContextHelper;
import cn.yixblog.support.mybatis.autosql.dialects.ISqlDialect;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * dialect manager impl
 * Created by yixian on 2015-09-02.
 */
public class SqlDialectManager {
    private static Map<String, ISqlDialect> dialectMap = new HashMap<>();

    public static ISqlDialect getDialect(String name) {
        String defaultDialect = ApplicationContextHelper.resolvePattern("${mybatis.autosql.default-dialect}");
        name = StringUtils.isEmpty(name) ? defaultDialect : name;
        return dialectMap.get(name.toLowerCase());
    }

    public static void register(ISqlDialect sqlDialect) {
        dialectMap.put(sqlDialect.getName().toLowerCase(), sqlDialect);
    }
}
