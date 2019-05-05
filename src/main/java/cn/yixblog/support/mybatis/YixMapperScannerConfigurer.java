package cn.yixblog.support.mybatis;

import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContextAware;

/**
 * most code copy from mybatis-spring
 * <p>
 * with loading basic mybatis mappers,also need to load auto-generation mapper
 * </p>
 * <p/>
 * created at 2015-09-01
 *
 * @author yixian
 * @see org.mybatis.spring.mapper.MapperScannerConfigurer
 */
public class YixMapperScannerConfigurer extends MapperScannerConfigurer implements BeanDefinitionRegistryPostProcessor, InitializingBean, ApplicationContextAware, BeanNameAware {

    public YixMapperScannerConfigurer() {
        super.setMapperFactoryBeanClass(YixMapperFactoryBean.class);
    }

    /**
     * This property lets you set the base package for your mapper interface files.
     * <p/>
     * You can set more than one package by using a semicolon or comma as a separator.
     * <p/>
     * Mappers will be searched for recursively starting in the specified package(s).
     *
     * @param basePackage base package name
     */
    @Override
    public void setBasePackage(String basePackage) {
        final String dialectMapperPackage = "cn.yixblog.support.mybatis.autosql.dialects.*.mappers";
        super.setBasePackage(basePackage + "," + dialectMapperPackage);
    }
}
