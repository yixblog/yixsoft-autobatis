package cn.yixblog.support.mybatis.utils;

import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.session.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * build json type resultMap
 * Created by yixian on 2015-09-02.
 */
public class ResultMapUtils {
    public static List<ResultMap> createJsonResultMap(Configuration configuration, String statementId, Class resultType) {
        ResultMap.Builder builder = new ResultMap.Builder(configuration, statementId + "-Inline", resultType, new ArrayList<ResultMapping>());
        ResultMap map = builder.build();
        List<ResultMap> maps = new ArrayList<>();
        maps.add(map);
        return maps;
    }
}
