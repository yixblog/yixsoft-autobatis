package com.yixsoft.support.mybatis.test;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.yixsoft.support.mybatis.test.mappers.BasicLogMapper;
import com.yixsoft.support.mybatis.test.mappers.ExampleAutoEntityMapper;
import com.yixsoft.support.mybatis.test.mappers.ExampleEntityMapper;
import com.yixsoft.support.mybatis.test.mappers.Log2Mapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * validate plugin
 * Created by yixian on 2015-08-31.
 */
@SpringBootTest
@Transactional
class TestBatisPlugin {
    private BasicLogMapper logMapper;
    private Log2Mapper log2Mapper;
    private ExampleEntityMapper expMapper;
    private ExampleAutoEntityMapper expAutoIdMapper;

    @Test
    @Rollback
    void validPlugin() {
        Map<String, Object> itm = logMapper.findOne("aba");
        Assertions.assertNotNull(itm, "aba instance should be exists");
        Map<String, Object> params = new HashMap<>();
        params.put("userid", "1");
        PageList<Map<String, Object>> list = logMapper.list(params, new PageBounds(1, 10));
        Assertions.assertFalse(list.isEmpty(), "userid=1 query result should not be empty");
        Integer count = logMapper.count(params);
        Assertions.assertEquals(2L, (long) count, "count result should be 2");

        PageList<Map<String, Object>> pageList1 = logMapper.pageListOne(params, new PageBounds(1, 10));
        Assertions.assertEquals(list.getPaginator().getTotalCount(), pageList1.getPaginator().getTotalCount(), "query result should be equals");

        PageList<Map<String, Object>> pageListFake = logMapper.pageListFake(params, new PageBounds(1, 10));
        Assertions.assertEquals(list.size(), pageListFake.size(), "list size should be same");
        Assertions.assertEquals(0, pageListFake.getPaginator().getTotalCount(), "fake count sql returns 0");

        Map<String, Object> newLog = new HashMap<>();
        newLog.put("content", "111");
        newLog.put("userid", "1");
        logMapper.save(newLog);
        String newLogId = newLog.get("id").toString();

        Assertions.assertNotNull(newLogId, "uuid generated");
        Map<String, Object> inserted = logMapper.findOne(newLogId);
        Assertions.assertNotNull(inserted, "object should have been saved into db");
        Assertions.assertEquals("111", inserted.get("content"), "content should be fetched correctly");

        Map<String, Object> demo = logMapper.findOneShort(newLogId);
        Assertions.assertNull(demo, "object should not exists when attach query params with advanceSelect");

        inserted.put("content", "222");
        inserted.put("id", Collections.singletonList(newLogId));
        logMapper.update(inserted);
        inserted = logMapper.findOne(newLogId);
        Assertions.assertEquals("222", inserted.get("content"),"update when id key is array should be accepted");

        logMapper.updateContentTo333(newLogId);
        inserted = logMapper.findOne(newLogId);
        Assertions.assertEquals("333", inserted.get("content"),"update when id key is array should be accepted");


        demo = logMapper.findOneShort(newLogId);
        Assertions.assertTrue(demo != null && !demo.containsKey("content"), "advance select should removed content column from result");

        logMapper.delete(newLogId);
        Assertions.assertNull(logMapper.findOne(newLogId), "delete should work");

        Map<String, Object> log2 = new HashMap<>();
        log2.put("name", "hhh");
        log2Mapper.save(log2);
        Assertions.assertNotNull(log2.get("pkid"), "autoincrement id can get");

        LogEntity log3 = new LogEntity().setUserid(123).setContent("bar");
        logMapper.saveEntity(log3);


    }

    @Test
    @Rollback
    void testScoreChange() {
        ExampleDAO dao = new ExampleDAO().setCreateTime(new Date()).setGroupName("foo");
        expMapper.save(dao);
        Assertions.assertNotNull(dao.getEntityId());

        ExampleDAO dao2 = expMapper.findOne(dao.getEntityId());
        Assertions.assertEquals("foo", dao2.getGroupName());

        dao2.setGroupName("bar");
        expMapper.update(dao2);
        dao2 = expMapper.findOne(dao.getEntityId());
        Assertions.assertEquals("bar", dao2.getGroupName());

        ExampleAnotherDAO another = new ExampleAnotherDAO().setTime(new Date()).setGname("etse");
        expMapper.save(another);

        ExampleDAO compare = expMapper.findOne(another.getEntityId());
        Assertions.assertEquals("etse", compare.getGroupName());
    }

    @Test
    @Rollback
    void testAutoIdWithCamelCase() {
        ExampleAutoIdDAO dao = new ExampleAutoIdDAO().setCreateTime(new Date()).setGroupName("foo");
        expAutoIdMapper.save(dao);
        Assertions.assertNotNull(dao.getEntityId());
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
