package cn.yixblog.support.mybatis.autosql.configuration.support.dialect;

import cn.yixblog.support.mybatis.autosql.configuration.support.ISqlDialectManager;
import cn.yixblog.support.mybatis.autosql.configuration.support.spring.ApplicationContextHelper;
import cn.yixblog.support.mybatis.autosql.dialects.ISqlDialect;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.binding.MapperProxy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * dialect manager impl
 * Created by yixian on 2015-09-02.
 */
@Component
public class SqlDialectManager implements ISqlDialectManager {
    private Map<String, ISqlDialect> dialectMap = new HashMap<>();

    @Resource
    public void setDialects(ISqlDialect[] dialects) {
        if (dialects != null) {
            for (ISqlDialect dialect : dialects) {
                if (Proxy.isProxyClass(dialect.getClass())){
                    continue;
                }
                dialectMap.put(dialect.getName().toLowerCase(), dialect);
            }
        }
    }

    @Override
    public ISqlDialect getDialect(String name) {
        String defaultDialect = ApplicationContextHelper.resolvePattern("${mybatis.autosql.default-dialect}");
        name = StringUtils.isEmpty(name) ? defaultDialect : name;
        return dialectMap.get(name.toLowerCase());
    }
}
