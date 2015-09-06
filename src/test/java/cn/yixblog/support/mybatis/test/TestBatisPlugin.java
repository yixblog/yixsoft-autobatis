package cn.yixblog.support.mybatis.test;

import cn.yixblog.support.mybatis.autosql.dialects.mysql.mappers.DescMySqlTableMapper;
import cn.yixblog.support.mybatis.test.mappers.BasicLogMapper;
import com.alibaba.fastjson.JSONObject;
import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import javax.annotation.Resource;
import java.util.List;

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
    @Resource
    private DescMySqlTableMapper tableMapper;
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
    }

}
