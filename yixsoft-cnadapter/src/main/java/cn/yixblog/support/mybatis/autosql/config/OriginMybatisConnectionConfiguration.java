package cn.yixblog.support.mybatis.autosql.config;

import com.yixsoft.support.mybatis.autosql.dialects.ISqlDialect;
import com.yixsoft.support.mybatis.dialects.mysql.MySqlDialect;
import com.yixsoft.support.mybatis.dialects.oracle.OracleDialect;
import com.yixsoft.support.mybatis.spring.MapperConfiguration;
import com.yixsoft.support.mybatis.spring.YixMyBatisConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureBefore(MapperConfiguration.class)
public class OriginMybatisConnectionConfiguration {

    @Bean
    public YixMyBatisConfig defaultConfig(@Value("${mybatis.autosql.default-dialect:mysql}") String defaultDialect) {
        org.apache.ibatis.session.Configuration properties = new org.apache.ibatis.session.Configuration();
        properties.setMapUnderscoreToCamelCase(true);
        Class<? extends ISqlDialect> sqlDialect = "oracle".equalsIgnoreCase(defaultDialect) ? OracleDialect.class : MySqlDialect.class;
        YixMyBatisConfig.PaginatorConfig paginatorConfig = new YixMyBatisConfig.PaginatorConfig()
                .setEnable(true)
                .setAdvancedCount(true)
                .setAsyncTotalCount(false)
                .setPoolMaxSize(0)
                .setDialect("oracle".equalsIgnoreCase(defaultDialect) ? "com.github.miemiedev.mybatis.paginator.dialect.OracleDialect" : "com.github.miemiedev.mybatis.paginator.dialect.MySQLDialect");
        return new YixMyBatisConfig()
                .setConfiguration(properties)
                .setDialects(new Class[]{sqlDialect})
                .setPaginator(paginatorConfig);

    }
}
