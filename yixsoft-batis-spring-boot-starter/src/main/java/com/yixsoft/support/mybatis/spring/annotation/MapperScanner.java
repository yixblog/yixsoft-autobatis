package com.yixsoft.support.mybatis.spring.annotation;

import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Will auto generate a MyBatisMapperScanner
 * Create by davep at 2019-12-18 16:30
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(MyBatisMapperScannerConfigurer.class)
public @interface MapperScanner {
    /**
     * Alias for the {@link #basePackages()} attribute. Allows for more concise
     * annotation declarations e.g.:
     * {@code @MapperScanner("org.my.pkg")} instead of {@code @MapperScanner(basePackages= "org.my.pkg"})}.
     *
     * @return packages to scan.
     */
    String[] value() default {};

    /**
     * Base packages to scan for MyBatis interfaces. Note that only interfaces
     * with at least one method will be registered; concrete classes will be
     * ignored.
     *
     * @return packages to scan
     */
    String[] basePackages() default {};

    /**
     * The {@link BeanNameGenerator} class to be used for naming detected components
     * within the Spring container.
     *
     * @return custom bean name generator class
     */
    Class<? extends BeanNameGenerator> nameGenerator() default BeanNameGenerator.class;

    /**
     * This property specifies the annotation that the scanner will search for.
     * <p>
     * The scanner will register all interfaces in the base package that also have
     * the specified annotation.
     * <p>
     * Note this can be combined with markerInterface.
     *
     * @return custom annotation class to scan
     */
    Class<? extends Annotation> annotationClass() default Annotation.class;

    /**
     * This property specifies the parent that the scanner will search for.
     * <p>
     * The scanner will register all interfaces in the base package that also have
     * the specified interface class as a parent.
     * <p>
     * Note this can be combined with annotationClass.
     *
     * @return customer parent interface class to scan
     */
    Class<?> markerInterface() default Class.class;

    /**
     * Specifies which {@code SqlSessionTemplate} to use in the case that there is
     * more than one in the spring context. Usually this is only needed when you
     * have more than one datasource.
     *
     * @return sqlSessionTemplate bean name, leave empty if there is only one sqlSessionTemplate
     */
    String sqlSessionTemplateRef() default "";

    /**
     * Specifies which {@code SqlSessionFactory} to use in the case that there is
     * more than one in the spring context. Usually this is only needed when you
     * have more than one datasource.
     *
     * @return sqlSessionFactory bean name, leave empty if there is only one sqlSessionFactory
     */
    String sqlSessionFactoryRef() default "";
}
