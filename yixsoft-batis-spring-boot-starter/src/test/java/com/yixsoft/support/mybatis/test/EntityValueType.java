package com.yixsoft.support.mybatis.test;

import com.yixsoft.support.mybatis.typehandlers.EnumValue;

public enum EntityValueType {
    GUEST(1),
    MEMBER(2),
    VIP(3);
    @EnumValue
    private final int code;

    EntityValueType(int code) {
        this.code = code;
    }
}
