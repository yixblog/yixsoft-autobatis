package cn.yixblog.support.mybatis.autosql.configuration.sqlsource;

import cn.yixblog.support.mybatis.autosql.configuration.support.config.SqlGenerationConfig;
import cn.yixblog.support.mybatis.autosql.core.IAutoSqlProvider;
import cn.yixblog.support.mybatis.utils.ResultMapUtils;
import org.apache.ibatis.builder.SqlSourceBuilder;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * auto generation sql source
 * Created by yixian on 2015-09-02.
 */
public class AutoSqlSource implements SqlSource {

    private Configuration configuration;
    private SqlGenerationConfig genConfig;

    public AutoSqlSource(String statementName, Configuration configuration, Method method) {
        this.configuration = configuration;
        genConfig = new SqlGenerationConfig(statementName, method);
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        IAutoSqlProvider provider = genConfig.newSqlProvider(parameterObject);
        SqlSource sqlSource = createSqlSource(parameterObject, provider);
        BoundSql boundSql = sqlSource.getBoundSql(parameterObject);

        Map<String, Object> additionalParams = provider.getAdditionalParams();
        if (!additionalParams.isEmpty()) {
            for (Map.Entry<String, Object> entry : additionalParams.entrySet()) {
                boundSql.setAdditionalParameter(entry.getKey(), entry.getValue());
            }
        }
        return boundSql;
    }

    private SqlSource createSqlSource(Object parameterObject, IAutoSqlProvider provider) {
        SqlSourceBuilder parser = new SqlSourceBuilder(configuration);
        Class paramType = parameterObject == null ? Object.class : parameterObject.getClass();
        return parser.parse(provider.getSql(), paramType, new HashMap<String, Object>());
    }

    public MappedStatement createStatement() {
        MappedStatement.Builder builder = new MappedStatement.Builder(configuration, genConfig.getStatementId(), this, genConfig.getType());
        builder.resultMaps(ResultMapUtils.createJsonResultMap(configuration, genConfig.getStatementId(), genConfig.getResultType()));
        return builder.build();
    }
}
