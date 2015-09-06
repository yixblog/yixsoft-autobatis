package cn.yixblog.support.mybatis.test;

import cn.yixblog.support.mybatis.test.mappers.BasicLogMapper;
import com.alibaba.fastjson.JSONObject;
import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import javax.annotation.Resource;

/**
 * validate plugin
 * Created by yixian on 2015-08-31.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
@TransactionConfiguration
public class TestBatisPlugin {
    @Resource
    private BasicLogMapper logMapper;
    @Test
    public void validPlugin(){
        JSONObject itm = logMapper.findOne("aba");
        assert itm!=null;
        JSONObject params = new JSONObject();
        params.put("userid","1");
        PageList<JSONObject> list = logMapper.list(params, new PageBounds(1,10));
        assert !list.isEmpty();
        Integer count= logMapper.count(params);
        assert count==2;

        JSONObject newLog = new JSONObject();
        newLog.put("Content", "111");
        newLog.put("userId","1");
        logMapper.save(newLog);
        String newLogId = newLog.getString("id");

        assert newLogId!=null;
        JSONObject inserted = logMapper.findOne(newLogId);
        assert inserted!=null;
        assert "111".equals(inserted.getString("content"));

        inserted.put("content","222");
        logMapper.update(inserted);
        inserted = logMapper.findOne(newLogId);
        assert "222".equals(inserted.getString("content"));


        logMapper.delete(newLogId);
        assert logMapper.findOne(newLogId)==null;

    }

}
