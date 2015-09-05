package cn.yixblog.support.mybatis.autosql.configuration.support.spring;

import cn.yixblog.support.mybatis.exceptions.ApplicationContextNotReadyException;
import org.springframework.context.ApplicationContext;

import java.util.Map;

/**
 * support java bean out of spring applicationContext get beans from spring
 * Created by yixian on 2015-09-02.
 */
public class ApplicationContextHelper {
    private static ApplicationContext ctx;

    public static void setApplicationContext(ApplicationContext context){
        ctx = context;
    }

    public static <T> Map<String, T> getBeansOfType(Class<T> type) {
        if (ctx == null) {
            throw new ApplicationContextNotReadyException();
        }
        return ctx.getBeansOfType(type);
    }

    public static Object getBean(String beanName) {
        if (ctx == null) {
            throw new ApplicationContextNotReadyException();
        }
        return ctx.getBean(beanName);
    }

    public static <T> T getBean(Class<T> type) {
        if (ctx == null) {
            throw new ApplicationContextNotReadyException();
        }
        return ctx.getBean(type);
    }

    public static String resolvePattern(String pattern){
        return ctx.getEnvironment().resolvePlaceholders(pattern);
    }

}
