package cn.yixblog.support.mybatis.autosql.configuration.support.spring;

import cn.yixblog.support.mybatis.exceptions.ApplicationContextNotReadyException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * support java bean out of spring applicationContext get beans from spring
 * Created by yixian on 2015-09-02.
 */
@Component
public class ApplicationContextHelper implements ApplicationContextAware {
    private static ApplicationContext ctx;

    public void setApplicationContext(ApplicationContext context) {
        ApplicationContextHelper.ctx = context;
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

    public static String resolvePattern(String pattern) {
        return ((GenericApplicationContext) ctx).getBeanFactory().resolveEmbeddedValue(pattern);
    }

}
