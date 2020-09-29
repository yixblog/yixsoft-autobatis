package com.yixsoft.support.mybatis.test;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.yixsoft.support.mybatis.test.mappers.BasicLogMapper;
import com.yixsoft.support.mybatis.test.mappers.ExampleAutoEntityMapper;
import com.yixsoft.support.mybatis.test.mappers.ExampleEntityMapper;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
    private ExampleEntityMapper expMapper;
    private ExampleAutoEntityMapper expAutoIdMapper;

    @Test
    @Rollback
    public void validPlugin() {
        Map<String, Object> itm = logMapper.findOne("aba");
        Assert.assertNotNull("aba instance should be exists", itm);
        Map<String, Object> params = new HashMap<>();
        params.put("userid", "1");
        PageList<Map<String, Object>> list = logMapper.list(params, new PageBounds(1, 10));
        Assert.assertFalse("userid=1 query result should not be empty", list.isEmpty());
        Integer count = logMapper.count(params);
        Assert.assertEquals("count result should be 2", 2L, (long) count);

        PageList<Map<String, Object>> pageList1 = logMapper.pageListOne(params, new PageBounds(1, 10));
        Assert.assertEquals("query result should be equals", list.getPaginator().getTotalCount(), pageList1.getPaginator().getTotalCount());

        PageList<Map<String, Object>> pageListFake = logMapper.pageListFake(params, new PageBounds(1, 10));
        Assert.assertEquals("list size should be same", list.size(), pageListFake.size());
        Assert.assertEquals("fake count sql returns 0", 0, pageListFake.getPaginator().getTotalCount());

        Map<String, Object> newLog = new HashMap<>();
        newLog.put("content", "111");
        newLog.put("userid", "1");
        logMapper.save(newLog);
        String newLogId = newLog.get("id").toString();

        Assert.assertNotNull("uuid generated", newLogId);
        Map<String, Object> inserted = logMapper.findOne(newLogId);
        Assert.assertNotNull("object should have been saved into db", inserted);
        Assert.assertEquals("content should be fetched correctly", "111", inserted.get("content"));

        Map<String, Object> demo = logMapper.findOneShort(newLogId);
        Assert.assertNull("object should not exists when attach query params with advanceSelect", demo);

        inserted.put("content", "222");
        inserted.put("id", Collections.singletonList(newLogId));
        logMapper.update(inserted);
        inserted = logMapper.findOne(newLogId);
        Assert.assertEquals("update when id key is array should be accepted", "222", inserted.get("content"));

        logMapper.updateContentTo333(newLogId);
        inserted = logMapper.findOne(newLogId);
        Assert.assertEquals("update when id key is array should be accepted", "333", inserted.get("content"));


        demo = logMapper.findOneShort(newLogId);
        Assert.assertTrue("advance select should removed content column from result", demo != null && !demo.containsKey("content"));

        logMapper.delete(newLogId);
        Assert.assertNull("delete should work", logMapper.findOne(newLogId));

        Map<String, Object> log2 = new HashMap<>();
        log2.put("name", "hhh");
        log2Mapper.save(log2);
        Assert.assertNotNull("autoincrement id can get", log2.get("pkid"));

        LogEntity log3 = new LogEntity().setUserid(123).setContent("bar");
        logMapper.saveEntity(log3);


    }

    @Test
    @Rollback
    public void testScoreChange() {
        ExampleDAO dao = new ExampleDAO().setCreateTime(new Date()).setGroupName("foo");
        expMapper.save(dao);
        Assert.assertNotNull(dao.getEntityId());

        ExampleDAO dao2 = expMapper.findOne(dao.getEntityId());
        Assert.assertEquals("foo", dao2.getGroupName());

        dao2.setGroupName("bar");
        expMapper.update(dao2);
        dao2 = expMapper.findOne(dao.getEntityId());
        Assert.assertEquals("bar", dao2.getGroupName());

        ExampleAnotherDAO another = new ExampleAnotherDAO().setTime(new Date()).setGname("etse");
        expMapper.save(another);

        ExampleDAO compare = expMapper.findOne(another.getEntityId());
        Assert.assertEquals("etse", compare.getGroupName());
    }

    @Test
    @Rollback
    public void testAutoIdWithCamelCase() {
        ExampleAutoIdDAO dao = new ExampleAutoIdDAO().setCreateTime(new Date()).setGroupName("foo");
        expAutoIdMapper.save(dao);
        Assert.assertNotNull(dao.getEntityId());
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

    @Autowired
    public TestBatisPlugin setExpMapper(ExampleEntityMapper expMapper) {
        this.expMapper = expMapper;
        return this;
    }

    @Autowired
    public TestBatisPlugin setExpAutoIdMapper(ExampleAutoEntityMapper expAutoIdMapper) {
        this.expAutoIdMapper = expAutoIdMapper;
        return this;
    }
}
