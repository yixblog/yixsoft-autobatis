package cn.yixblog.support.mybatis.paginator.config;

import cn.yixblog.support.mybatis.paginator.annotations.AdvancedPaginator;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.binding.BindingException;
import org.apache.ibatis.builder.SqlSourceBuilder;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Create by yixian at 2019-05-05 13:12
 */
public class AdvancedPaginationConfigHandler {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private static volatile AdvancedPaginationConfigHandler handler;
    private final Map<String, Boolean> statementConfigMap = new HashMap<>();
    private final Map<String, String> countStatementRef = new HashMap<>();

    public static AdvancedPaginationConfigHandler getHandler() {
        if (handler == null) {
            synchronized (AdvancedPaginationConfigHandler.class) {
                if (handler == null) {
                    handler = new AdvancedPaginationConfigHandler();
                }
            }
        }
        return handler;
    }

    private AdvancedPaginationConfigHandler() {
    }

    public synchronized MappedStatement resolveCountStatement(Configuration configuration, String queryStatementId, Object parameter) {
        if (!statementConfigMap.containsKey(queryStatementId)) {
            MappedStatement countStatement = resolveAndBuildStatement(configuration, queryStatementId, parameter);
            if (countStatement == null) {
                statementConfigMap.put(queryStatementId, false);
            } else {
                countStatementRef.put(queryStatementId, countStatement.getId());
                statementConfigMap.put(queryStatementId, true);
            }
            return countStatement;
        }

        if (statementConfigMap.get(queryStatementId)) {
            String countStatementId = countStatementRef.get(queryStatementId);
            Assert.notNull(countStatementId, "count statement id should be put before exists check map");
            return configuration.getMappedStatement(countStatementId);
        } else {
            return null;
        }
    }

    private MappedStatement resolveAndBuildStatement(Configuration configuration, String queryStatementId, Object parameter) {
        if (queryStatementId.contains(".")) {
            String className = StringUtils.substringBeforeLast(queryStatementId, ".");
            String methodName = StringUtils.substringAfterLast(queryStatementId, ".");
            try {
                Class mapperClass = Class.forName(className);
                Method[] methods = mapperClass.getDeclaredMethods();
                Method targetMethod = Arrays.stream(methods).filter(mth -> mth.getName().equals(methodName)).findFirst().orElse(null);
                if (targetMethod != null && AnnotatedElementUtils.isAnnotated(targetMethod, AdvancedPaginator.class)) {
                    AdvancedPaginator paginatorConfig = AnnotatedElementUtils.getMergedAnnotation(targetMethod, AdvancedPaginator.class);
                    Assert.notNull(paginatorConfig, "AdvancedPaginator should be annotated");
                    return buildCountStatement(queryStatementId, paginatorConfig, parameter, configuration);
                }
            } catch (ClassNotFoundException ignore) {
                logger.debug("mapper class {} not found, skip", className);
            }
        }
        return null;
    }

    private MappedStatement buildCountStatement(String statementId, AdvancedPaginator paginatorConfig, Object parameter, Configuration configuration) {
        String countRef = paginatorConfig.countRef();
        if (StringUtils.isNotEmpty(countRef)) {
            if (countRef.startsWith(".")) {
                String className = StringUtils.substringBeforeLast(statementId, ".");
                countRef = className + countRef;
            }
            if (configuration.hasStatement(countRef)) {
                return configuration.getMappedStatement(countRef);
            } else {
                throw new BindingException("Count statement not found:" + countRef);
            }
        }
        if (StringUtils.isNotEmpty(paginatorConfig.countSql())) {
            String countStatementId = statementId + "__count_ref";
            if (configuration.hasStatement(countStatementId)) {
                return configuration.getMappedStatement(countStatementId);
            }
            SqlSourceBuilder builder = new SqlSourceBuilder(configuration);
            SqlSource sqlSource = builder.parse(paginatorConfig.countSql(), parameter.getClass(), new HashMap<>());
            MappedStatement countStatement = new MappedStatement.Builder(configuration, countStatementId, sqlSource, SqlCommandType.SELECT).build();
            configuration.addMappedStatement(countStatement);
            return countStatement;
        }
        throw new BindingException("Advanced paginator statement not configured:" + statementId);
    }
}
