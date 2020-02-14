package com.yixsoft.support.mybatis.test.utilsTest;

import com.yixsoft.support.mybatis.utils.MapperMethodUtils;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Created by yixian on 2015-09-03.
 */
public class MethodReturnTypeUtilsTest {
    public List<Map<String,Object>> method() {
        return null;
    }

    @Test
    public void test() throws NoSuchMethodException {
        Method method = getClass().getMethod("method");
        Class clazz = MapperMethodUtils.getReturnType(method);
        assert Map.class.isAssignableFrom(clazz);
    }
}
