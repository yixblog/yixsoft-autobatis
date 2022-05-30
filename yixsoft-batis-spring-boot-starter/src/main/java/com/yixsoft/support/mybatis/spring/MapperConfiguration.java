package com.yixsoft.support.mybatis.spring;

import com.github.miemiedev.mybatis.paginator.OffsetLimitInterceptor;
import com.yixsoft.support.mybatis.YixMapperFactoryBean;
import com.yixsoft.support.mybatis.plugins.AdvancedPaginationInterceptor;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

/**
 * Create by davep at 2019-12-25 10:07
 */
@Configuration
@ConditionalOnClass({SqlSessionFactory.class, SqlSessionFactoryBean.class})
@ConditionalOnSingleCandidate(DataSource.class)
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
@AutoConfigureBefore(
        name = {"org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration"}
)
public class MapperConfiguration {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ResourceLoader resourceLoader;
    private final DatabaseIdProvider databaseIdProvider;

    public MapperConfiguration(ResourceLoader resourceLoader,
                               ObjectProvider<DatabaseIdProvider> databaseIdProviders) {
        this.resourceLoader = resourceLoader;
        this.databaseIdProvider = databaseIdProviders.getIfAvailable();
    }

    @Bean
    @ConditionalOnMissingBean(YixMyBatisConfig.class)
    @ConfigurationProperties(prefix = "mybatis")
    public YixMyBatisConfig yixMyBatisConfig() {
        return new YixMyBatisConfig();
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource,
                                               YixMyBatisConfig config,
                                               ObjectProvider<Interceptor[]> interceptorProvider) throws Exception {
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        if (StringUtils.hasText(config.getConfigLocation())) {
            factory.setConfigLocation(this.resourceLoader.getResource(config.getConfigLocation()));
        }
        applyConfiguration(factory, config);
        Interceptor[] interceptors = interceptorProvider.getIfAvailable();
        if (interceptors != null) {
            factory.setPlugins(interceptors);
        }
        if (databaseIdProvider != null) {
            factory.setDatabaseIdProvider(databaseIdProvider);
        }
        return factory.getObject();
    }

    private void applyConfiguration(SqlSessionFactoryBean factory, YixMyBatisConfig config) {
        org.apache.ibatis.session.Configuration configuration = config.getConfiguration();
        if (configuration == null && !StringUtils.hasText(config.getConfigLocation())) {
            configuration = new org.apache.ibatis.session.Configuration();
        }
        YixMapperFactoryBean.setDefaultKeyGen(config.getDefaultKeyGenerator());
        factory.setConfiguration(configuration);
    }

    @Bean
    @ConditionalOnMissingBean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Bean
    @ConditionalOnProperty(value = "mybatis.paginator.enable", havingValue = "true")
    public OffsetLimitInterceptor offsetLimitInterceptor(YixMyBatisConfig config) {
        OffsetLimitInterceptor interceptor = new OffsetLimitInterceptor();
        config.configOffsetLimitInterceptor(interceptor);
        return interceptor;
    }

    @Bean
    @ConditionalOnBean(OffsetLimitInterceptor.class)
    public AdvancedPaginationInterceptor advancedPaginationInterceptor(YixMyBatisConfig config) {
        return new AdvancedPaginationInterceptor(config.getPaginator().isEnable());
    }

}
