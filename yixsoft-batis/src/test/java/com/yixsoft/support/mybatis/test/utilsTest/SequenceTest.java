package com.yixsoft.support.mybatis.test.utilsTest;

import com.yixsoft.support.mybatis.support.snowflake.StringSequence;
import com.yixsoft.support.mybatis.support.snowflake.ljf.Sequence;
import org.junit.Test;

public class SequenceTest {

    @Test
    public void testSequence(){
        Sequence sequence = new Sequence();
        System.out.println("sequenceid:"+sequence.nextId());
    }

    @Test
    public void testStrSequence(){
        StringSequence sequence = new StringSequence();
        System.out.println("sequenceid:"+sequence.nextId());
    }
}
