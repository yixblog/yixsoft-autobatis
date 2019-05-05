package cn.yixblog.support.mybatis.plugins;

import cn.yixblog.support.mybatis.paginator.config.AdvancedPaginationConfigHandler;
import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.github.miemiedev.mybatis.paginator.domain.Paginator;
import com.github.miemiedev.mybatis.paginator.support.DefaultParameterHandler;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.jdbc.PreparedStatementLogger;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * Create by yixian at 2019-04-30 18:24
 */
@Intercepts({@Signature(
        type = Executor.class,
        method = "query",
        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})})
public class AdvancedPaginationInterceptor implements Interceptor {
    private static Logger logger = LoggerFactory.getLogger(AdvancedPaginationInterceptor.class);
    static int MAPPED_STATEMENT_INDEX = 0;
    static int PARAMETER_INDEX = 1;
    static int ROWBOUNDS_INDEX = 2;
    static int RESULT_HANDLER_INDEX = 3;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        final Object[] queryArgs = invocation.getArgs();
        final MappedStatement ms = (MappedStatement) queryArgs[MAPPED_STATEMENT_INDEX];

        final Object parameter = queryArgs[PARAMETER_INDEX];
        String statementId = ms.getId();
        MappedStatement countStatement = AdvancedPaginationConfigHandler.getHandler().resolveCountStatement(ms.getConfiguration(), statementId, parameter);
        if (countStatement != null) {
            return proceedAdvancedPagination(invocation, countStatement, queryArgs);
        }
        return invocation.proceed();
    }


    private Object proceedAdvancedPagination(Invocation invocation, MappedStatement countStatement, Object[] queryArgs) throws ExecutionException, InterruptedException {
        final Executor executor = (Executor) invocation.getTarget();
        final Object parameter = queryArgs[PARAMETER_INDEX];
        final RowBounds rowBounds = (RowBounds) queryArgs[ROWBOUNDS_INDEX];
        final PageBounds pageBounds = new PageBounds(rowBounds);
        final boolean containsTotalCount = pageBounds.isContainsTotalCount();
        pageBounds.setContainsTotalCount(false);
        queryArgs[ROWBOUNDS_INDEX] = pageBounds;
        Future<List> listFuture = call((Callable<List>) () -> (List) invocation.proceed());


        if (containsTotalCount) {
            Callable<Paginator> countTask = () -> {
                int count = count(executor, countStatement, parameter);

                return new Paginator(pageBounds.getPage(), pageBounds.getLimit(), count);
            };
            Future<Paginator> countFutrue = call(countTask);
            return new PageList(listFuture.get(), countFutrue.get());
        }

        return listFuture.get();
    }

    private int count(Executor executor, MappedStatement countStatement, Object parameter) throws SQLException {
        final BoundSql countSql = countStatement.getBoundSql(parameter);
        final Connection connection = executor.getTransaction().getConnection();
        String sql = countSql.getSql();
        Log mappingLogger = countStatement.getStatementLog();

        mappingLogger.debug("==>  Preparing: " + sql);
        PreparedStatement countStm = PreparedStatementLogger.newInstance(connection.prepareStatement(sql), mappingLogger, 1);
        DefaultParameterHandler handler = new DefaultParameterHandler(countStatement, parameter, countSql);
        handler.setParameters(countStm);

        try (ResultSet rs = countStm.executeQuery()) {
            int count = 0;
            if (rs.next()) {
                count = rs.getInt(1);
            }
            mappingLogger.debug("<==      Count: " + count);
            return count;
        }
    }

    private <T> Future<T> call(Callable callable) {
        FutureTask<T> future = new FutureTask(callable);
        future.run();
        return future;
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
