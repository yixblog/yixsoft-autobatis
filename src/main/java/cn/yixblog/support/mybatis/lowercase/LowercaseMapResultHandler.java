package cn.yixblog.support.mybatis.lowercase;

import cn.yixblog.support.mybatis.utils.LowerMapUtils;
import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;

import java.util.ArrayList;
import java.util.List;


/**
 * result handler to lowercase all keys
 * Created by yixian on 2015-08-31.
 */
public class LowercaseMapResultHandler implements ResultHandler {
    private List<Object> result = new ArrayList<>();

    @Override
    public void handleResult(ResultContext resultContext) {
        Object obj = resultContext.getResultObject();
        if (obj instanceof JSONObject) {
            JSONObject item = LowerMapUtils.lowercase((JSONObject) obj);
            result.add(item);
        }
    }


    public List<Object> getResult() {
        return result;
    }
}
