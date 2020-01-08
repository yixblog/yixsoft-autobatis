package com.yixsoft.support.mybatis.test;

import com.alibaba.fastjson.JSONObject;
import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.yixsoft.support.mybatis.test.mappers.BasicLogMapper;
import com.yixsoft.support.mybatis.test.mappers.Log2Mapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

/**
 * validate plugin
 * Created by yixian on 2015-08-31.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class TestBatisPlugin {
    private BasicLogMapper logMapper;
    private Log2Mapper log2Mapper;

    @Test
    @Rollback
    public void validPlugin() {
        JSONObject itm = logMapper.findOne("aba");
        Assert.assertNotNull("aba instance should be exists", itm);
        JSONObject params = new JSONObject();
        params.put("userid", "1");
        PageList<JSONObject> list = logMapper.list(params, new PageBounds(1, 10));
        Assert.assertFalse("userid=1 query result should not be empty", list.isEmpty());
        Integer count = logMapper.count(params);
        Assert.assertEquals("count result should be 2", 2L, (long) count);

        PageList<JSONObject> pageList1 = logMapper.pageListOne(params, new PageBounds(1, 10));
        Assert.assertEquals("query result should be equals", list.getPaginator().getTotalCount(), pageList1.getPaginator().getTotalCount());

        PageList<JSONObject> pageListFake = logMapper.pageListFake(params, new PageBounds(1, 10));
        Assert.assertEquals("list size should be same", list.size(), pageListFake.size());
        Assert.assertEquals("fake count sql returns 0", 0, pageListFake.getPaginator().getTotalCount());

        JSONObject newLog = new JSONObject();
        newLog.put("Content", "111");
        newLog.put("userId", "1");
        logMapper.save(newLog);
        String newLogId = newLog.getString("id");

        Assert.assertNotNull("uuid generated", newLogId);
        JSONObject inserted = logMapper.findOne(newLogId);
        Assert.assertNotNull("object should have been saved into db", inserted);
        Assert.assertEquals("content should be fetched correctly", "111", inserted.getString("content"));

        JSONObject demo = logMapper.findOneShort(newLogId);
        Assert.assertNull("object should not exists when attach query params with advanceSelect", demo);

        inserted.put("content", "222");
        inserted.put("id", Collections.singletonList(newLogId));
        logMapper.update(inserted);
        inserted = logMapper.findOne(newLogId);
        Assert.assertEquals("update when id key is array should be accepted", "222", inserted.getString("content"));

        logMapper.updateContentTo333(newLogId);
        inserted = logMapper.findOne(newLogId);
        Assert.assertEquals("update when id key is array should be accepted", "333", inserted.getString("content"));


        demo = logMapper.findOneShort(newLogId);
        Assert.assertTrue("advance select should removed content column from result", demo != null && !demo.containsKey("content"));

        logMapper.delete(newLogId);
        Assert.assertNull("delete should work", logMapper.findOne(newLogId));

        JSONObject log2 = new JSONObject();
        log2.put("name", "hhh");
        log2Mapper.save(log2);
        Assert.assertNotNull("autoincrement id can get", log2.getInteger("pkid"));

        LogEntity log3 = new LogEntity().setUserid(123).setContent("bar");
        logMapper.saveEntity(log3);
        Assert.assertNotNull(log3.getId());
    }

    @Autowired
    public TestBatisPlugin setLogMapper(BasicLogMapper logMapper) {
        this.logMapper = logMapper;
        return this;
    }

    @Autowired
    public TestBatisPlugin setLog2Mapper(Log2Mapper log2Mapper) {
        this.log2Mapper = log2Mapper;
        return this;
    }
}
