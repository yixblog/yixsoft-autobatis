package com.yixsoft.support.mybatis.typehandlers;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface EnumValue {
}
