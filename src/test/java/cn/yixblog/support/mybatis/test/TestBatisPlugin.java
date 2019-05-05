package cn.yixblog.support.mybatis.test;

import cn.yixblog.support.mybatis.test.mappers.BasicLogMapper;
import cn.yixblog.support.mybatis.test.mappers.Log2Mapper;
import com.alibaba.fastjson.JSONObject;
import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;

import static org.junit.Assert.*;

/**
 * validate plugin
 * Created by yixian on 2015-08-31.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
@Rollback
public class TestBatisPlugin {
    private BasicLogMapper logMapper;
    private Log2Mapper log2Mapper;

    @Test
    public void validPlugin() {
        JSONObject itm = logMapper.findOne("aba");
        assertNotNull("aba instance should be exists", itm);
        JSONObject params = new JSONObject();
        params.put("userid", "1");
        PageList<JSONObject> list = logMapper.list(params, new PageBounds(1, 10));
        assertFalse("userid=1 query result should not be empty", list.isEmpty());
        Integer count = logMapper.count(params);
        assertEquals("count result should be 2", 2L, (long) count);

        PageList<JSONObject> pageList1 = logMapper.pageListOne(params, new PageBounds(1, 10));
        assertEquals("query result should be equals", list.getPaginator().getTotalCount(), pageList1.getPaginator().getTotalCount());

        PageList<JSONObject> pageListFake = logMapper.pageListFake(params, new PageBounds(1, 10));
        assertEquals("list size should be same", list.size(), pageListFake.size());
        assertEquals("fake count sql returns 0", 0, pageListFake.getPaginator().getTotalCount());

        JSONObject newLog = new JSONObject();
        newLog.put("Content", "111");
        newLog.put("userId", "1");
        logMapper.save(newLog);
        String newLogId = newLog.getString("id");

        assertNotNull("uuid generated", newLogId);
        JSONObject inserted = logMapper.findOne(newLogId);
        assertNotNull("object should have been saved into db", inserted);
        assertEquals("content should be fetched correctly", "111", inserted.getString("content"));

        JSONObject demo = logMapper.findOneShort(newLogId);
        assertNull("object should not exists when attach query params with advanceSelect", demo);

        inserted.put("content", "222");
        inserted.put("id", Collections.singletonList(newLogId));
        logMapper.update(inserted);
        inserted = logMapper.findOne(newLogId);
        assertEquals("update when id key is array should be accepted", "222", inserted.getString("content"));

        demo = logMapper.findOneShort(newLogId);
        assertTrue("advance select should removed content column from result", demo != null && !demo.containsKey("content"));

        logMapper.delete(newLogId);
        assertNull("delete should work", logMapper.findOne(newLogId));

        JSONObject log2 = new JSONObject();
        log2.put("name", "hhh");
        log2Mapper.save(log2);
        assertNotNull("autoincrement id can get", log2.getInteger("pkid"));
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
