package com.yixsoft.support.mybatis.test;

import org.apache.ibatis.jdbc.SQL;
import org.junit.Test;


/**
 * Created by yixian on 2016-04-24.
 */
public class SqlBuilderTest {

    @Test
    public void test() {
        SQL sql = new SQL();
        sql.SELECT("*").
                FROM("tablesample").
                WHERE("1=1").
                WHERE("AND a=1");
        System.out.println(sql.toString());
    }
}
