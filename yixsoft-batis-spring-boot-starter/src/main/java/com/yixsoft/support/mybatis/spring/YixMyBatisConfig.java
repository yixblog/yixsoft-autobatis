package com.yixsoft.support.mybatis.spring;

import com.github.miemiedev.mybatis.paginator.OffsetLimitInterceptor;
import com.yixsoft.support.mybatis.autosql.dialects.ISqlDialect;
import com.yixsoft.support.mybatis.autosql.dialects.SqlDialectManager;
import com.yixsoft.support.mybatis.autosql.dialects.SupportsDatabase;
import org.apache.ibatis.session.Configuration;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.Assert;

import java.util.Properties;

/**
 * Create by davep at 2019-12-17 18:28
 */
@ConfigurationProperties(prefix = "mybatis")
public class YixMyBatisConfig implements InitializingBean {
    private String configLocation;
    private Properties configurationProperties;
    @NestedConfigurationProperty
    private Configuration configuration;
    private PaginatorConfig paginator;
    private Class<? extends ISqlDialect>[] dialects;

    public String getConfigLocation() {
        return configLocation;
    }

    public YixMyBatisConfig setConfigLocation(String configLocation) {
        this.configLocation = configLocation;
        return this;
    }

    public Properties getConfigurationProperties() {
        return configurationProperties;
    }

    public YixMyBatisConfig setConfigurationProperties(Properties configurationProperties) {
        this.configurationProperties = configurationProperties;
        return this;
    }

    public YixMyBatisConfig setConfiguration(Configuration configuration) {
        this.configuration = configuration;
        return this;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public YixMyBatisConfig setDialects(Class<? extends ISqlDialect>[] dialects) {
        this.dialects = dialects;
        return this;
    }

    public Class<? extends ISqlDialect>[] getDialects() {
        return dialects;
    }

    @Override
    public void afterPropertiesSet() {
        if (paginator != null && paginator.isEnable()) {
            Assert.notNull(paginator.getDialect(), "Paginator dialect not determined");
        }
        if (dialects != null) {
            for (Class<? extends ISqlDialect> dialectClazz : dialects) {
                if (AnnotatedElementUtils.isAnnotated(dialectClazz, SupportsDatabase.class)) {
                    SupportsDatabase supports = AnnotatedElementUtils.getMergedAnnotation(dialectClazz, SupportsDatabase.class);
                    Assert.notNull(supports, "Annotation should not be null as checked before");
                    for (String db : supports.value()) {
                        SqlDialectManager.register(db, dialectClazz);
                    }
                }
            }
        }
    }

    public void configOffsetLimitInterceptor(OffsetLimitInterceptor interceptor) {
        interceptor.setDialectClass(paginator.getDialect());
        if (paginator.getPoolMaxSize() > 0) {
            interceptor.setPoolMaxSize(paginator.getPoolMaxSize());
        }
        interceptor.setAsyncTotalCount(paginator.isAsyncTotalCount());
    }

    public PaginatorConfig getPaginator() {
        return paginator;
    }

    public YixMyBatisConfig setPaginator(PaginatorConfig paginator) {
        this.paginator = paginator;
        return this;
    }

    public static class PaginatorConfig {
        private boolean enable;
        private String dialect;
        private int poolMaxSize = 0;
        private boolean asyncTotalCount;
        private boolean advancedCount;

        public boolean isEnable() {
            return enable;
        }

        public PaginatorConfig setEnable(boolean enable) {
            this.enable = enable;
            return this;
        }

        public String getDialect() {
            return dialect;
        }

        public PaginatorConfig setDialect(String dialect) {
            this.dialect = dialect;
            return this;
        }

        public int getPoolMaxSize() {
            return poolMaxSize;
        }

        public PaginatorConfig setPoolMaxSize(int poolMaxSize) {
            this.poolMaxSize = poolMaxSize;
            return this;
        }

        public boolean isAsyncTotalCount() {
            return asyncTotalCount;
        }

        public PaginatorConfig setAsyncTotalCount(boolean asyncTotalCount) {
            this.asyncTotalCount = asyncTotalCount;
            return this;
        }

        public boolean isAdvancedCount() {
            return advancedCount;
        }

        public PaginatorConfig setAdvancedCount(boolean advancedCount) {
            this.advancedCount = advancedCount;
            return this;
        }
    }
}
