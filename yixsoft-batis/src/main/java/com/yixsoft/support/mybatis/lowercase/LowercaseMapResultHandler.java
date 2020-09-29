package com.yixsoft.support.mybatis.lowercase;

import com.yixsoft.support.mybatis.utils.LowerMapUtils;
import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * result handler to lowercase all keys
 * Created by yixian on 2015-08-31.
 */
public class LowercaseMapResultHandler implements ResultHandler {
    private final List<Object> result = new ArrayList<>();

    @Override
    public void handleResult(ResultContext resultContext) {
        Object obj = resultContext.getResultObject();
        if (obj instanceof Map) {
            Map<String, Object> item = LowerMapUtils.lowercase((Map<?, ?>) obj);
            result.add(item);
        } else {
            result.add(obj);
        }
    }


    public List<Object> getResult() {
        return result;
    }
}
