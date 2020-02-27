package com.yixsoft.support.mybatis.test.utilsTest;

import com.google.common.base.CaseFormat;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Create by davep at 2020-02-27 12:34
 */
public class BeanDescribeTest {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void test() throws IntrospectionException {
        List<String> attributes = new ArrayList<>(Collections.list(Introspector.getBeanInfo(ExampleDAO.class).getBeanDescriptor().attributeNames()));
        List<PropertyDescriptor> methods = Arrays.stream(Introspector.getBeanInfo(ExampleDAO.class).getPropertyDescriptors())
                .collect(Collectors.toList());
        logger.info("attributes:{}", String.join(",", attributes));
        logger.info("methods:{}", methods.stream().map(PropertyDescriptor::getReadMethod)
                .map(Method::getName).collect(Collectors.joining(",")));

    }

    private List<String> filterMethodNames(Method method) {
        String methodName = method.getName();
        String fieldName = methodName.replaceFirst("(is|get)", "");
        List<String> convertedMethodNames = Arrays.stream(CaseFormat.values())
                .flatMap(sFormat -> Arrays.stream(CaseFormat.values())
                        .map(tFormat -> sFormat.to(tFormat, fieldName)))
                .distinct()
                .collect(Collectors.toList());
        logger.info("fieldNames:{}", String.join(",", convertedMethodNames));
        return convertedMethodNames;
    }

    public static class ExampleDAO {
        private String entityId;
        private String groupName;
        private Date createTime;
        private String remark;
        private boolean isValid;

        public ExampleDAO() {
        }

        public String getId() {
            return entityId;
        }

        public String test() {
            return "aaa";
        }


        public String getEntityId() {
            return entityId;
        }

        public ExampleDAO setEntityId(String entityId) {
            this.entityId = entityId;
            return this;
        }

        public String getGroupName() {
            return groupName;
        }

        public ExampleDAO setGroupName(String groupName) {
            this.groupName = groupName;
            return this;
        }

        public Date getCreateTime() {
            return createTime;
        }

        public ExampleDAO setCreateTime(Date createTime) {
            this.createTime = createTime;
            return this;
        }

        public String getRemark() {
            return remark;
        }

        public ExampleDAO setRemark(String remark) {
            this.remark = remark;
            return this;
        }

        public boolean isValid() {
            return isValid;
        }

        public ExampleDAO setValid(boolean valid) {
            isValid = valid;
            return this;
        }
    }
}
