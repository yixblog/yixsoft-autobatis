package com.yixsoft.support.mybatis.test;

import com.yixsoft.support.mybatis.spring.annotation.MapperScanner;
import org.springframework.context.annotation.Configuration;


/**
 * Create by davep at 2019-12-24 16:53
 */
@MapperScanner(basePackages = "com.yixsoft.support.mybatis.test.mappers")
@Configuration
public class MappersConfiguration {
}
