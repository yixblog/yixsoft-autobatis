package com.yixsoft.support.mybatis.spring;

import com.github.miemiedev.mybatis.paginator.OffsetLimitInterceptor;
import com.yixsoft.support.mybatis.autosql.dialects.ISqlDialect;
import com.yixsoft.support.mybatis.autosql.dialects.SqlDialectManager;
import com.yixsoft.support.mybatis.autosql.dialects.SupportsDatabase;
import com.yixsoft.support.mybatis.autosql.pk.UUIDPkProvider;
import com.yixsoft.support.mybatis.typehandlers.YixBatisEnumTypeHandler;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.session.Configuration;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Properties;

/**
 * Create by davep at 2019-12-17 18:28
 */
public class YixMyBatisConfig implements InitializingBean, ResourceLoaderAware {
    private String configLocation;
    private Properties configurationProperties;
    @NestedConfigurationProperty
    private Configuration configuration;
    private boolean enableStrictTableCheck = false;
    private PaginatorConfig paginator;
    private Class<? extends ISqlDialect>[] dialects;
    private Class<? extends KeyGenerator> defaultKeyGenerator = UUIDPkProvider.class;
    private ResourceLoader resourceLoader;

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

    public Class<? extends KeyGenerator> getDefaultKeyGenerator() {
        return defaultKeyGenerator;
    }

    public YixMyBatisConfig setDefaultKeyGenerator(Class<? extends KeyGenerator> defaultKeyGenerator) {
        this.defaultKeyGenerator = defaultKeyGenerator;
        return this;
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

        if (StringUtils.hasText(getConfigLocation())) {
            Resource resource = this.resourceLoader.getResource(getConfigLocation());
            Assert.state(resource.exists(), "Cannot find config location: " + resource + " (please add config file or check your Mybatis configuration)");
        }
        if (configuration != null) {
            configuration.setDefaultEnumTypeHandler(YixBatisEnumTypeHandler.class);
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

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public boolean isEnableStrictTableCheck() {
        return enableStrictTableCheck;
    }

    public YixMyBatisConfig setEnableStrictTableCheck(boolean enableStrictTableCheck) {
        this.enableStrictTableCheck = enableStrictTableCheck;
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
