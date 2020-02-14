package com.yixsoft.support.mybatis.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * lower map utils
 * Created by yixian on 2015-08-31.
 */
public class LowerMapUtils {
    public static Map<String, Object> lowercase(Map<?, ?> obj) {
        Map<String, Object> item = new HashMap<>();
        for (Map.Entry<?, ?> entry : obj.entrySet()) {
            if (entry.getKey() == null) {
                continue;
            }
            item.put(entry.getKey().toString().toLowerCase(), entry.getValue());
        }
        return item;
    }
}
