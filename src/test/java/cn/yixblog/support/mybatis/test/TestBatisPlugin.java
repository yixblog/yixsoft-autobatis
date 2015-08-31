package cn.yixblog.support.mybatis.test;

import cn.yixblog.support.mybatis.test.mappers.BasicLogMapper;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * validate plugin
 * Created by yixian on 2015-08-31.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class TestBatisPlugin {
    @Resource
    private BasicLogMapper logMapper;
    @Test
    public void validPlugin(){
        List<JSONObject> list = logMapper.list();
        assert !list.isEmpty();
        JSONObject itm = logMapper.findOne();
        assert itm!=null;
    }

}
