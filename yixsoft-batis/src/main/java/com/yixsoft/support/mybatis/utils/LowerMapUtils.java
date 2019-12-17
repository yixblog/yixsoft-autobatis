package com.yixsoft.support.mybatis.utils;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

/**
 * lower map utils
 * Created by yixian on 2015-08-31.
 */
public class LowerMapUtils {
    public static JSONObject lowercase(JSONObject obj) {
        JSONObject item = new JSONObject();
        for (Map.Entry<String,Object> entry: obj.entrySet()){
            item.put(entry.getKey().toLowerCase(),entry.getValue());
        }
        return item;
    }
}
