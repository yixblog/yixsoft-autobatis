package cn.yixblog.support.mybatis.test.utilsTest;

import cn.yixblog.support.mybatis.utils.MapperMethodUtils;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by yixian on 2015-09-03.
 */
public class MethodReturnTypeUtilsTest {
    public List<JSONObject> method(){
        return null;
    }

    @Test
    public void test() throws NoSuchMethodException {
        Method method= getClass().getMethod("method");
        Class clazz = MapperMethodUtils.getReturnType(method);
        assert JSONObject.class.isAssignableFrom(clazz);
    }
}
