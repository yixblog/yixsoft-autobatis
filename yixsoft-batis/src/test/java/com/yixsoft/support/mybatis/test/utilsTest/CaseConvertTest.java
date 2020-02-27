package com.yixsoft.support.mybatis.test.utilsTest;

import com.google.common.base.CaseFormat;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Create by davep at 2020-02-27 12:10
 */
public class CaseConvertTest {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void test() {
        String camelCase1 = "userName";
        String camelCase2 = "UserName";
        String snakeCase = "user_name";
        String[] sources = {camelCase1, camelCase2, snakeCase};
        for (String source : sources) {
            Assert.assertEquals(camelCase1, convert(CaseFormat.LOWER_CAMEL, source));
            Assert.assertEquals(snakeCase, convert(CaseFormat.LOWER_UNDERSCORE, source));
        }
    }

    private String convert(CaseFormat targetFormat, String source) {
        for (CaseFormat format : CaseFormat.values()) {
            String res = format.to(targetFormat, source);
            logger.info("{}->{}:{}->{}", format, targetFormat, source, res);
        }
        return source;
    }
}
