package com.yixsoft.support.mybatis.test;

import java.util.Date;

/**
 * Create by davep at 2020-02-25 2:40
 */
public class ExampleAutoIdDAO {
    private Integer entityId;
    private String groupName;
    private Date createTime;
    private String remark;
    private boolean isValid;

    public ExampleAutoIdDAO() {
    }

    public Integer getEntityId() {
        return entityId;
    }

    public ExampleAutoIdDAO setEntityId(Integer entityId) {
        this.entityId = entityId;
        return this;
    }

    public String getGroupName() {
        return groupName;
    }

    public ExampleAutoIdDAO setGroupName(String groupName) {
        this.groupName = groupName;
        return this;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public ExampleAutoIdDAO setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    public String getRemark() {
        return remark;
    }

    public ExampleAutoIdDAO setRemark(String remark) {
        this.remark = remark;
        return this;
    }

    public boolean getValid() {
        return isValid;
    }

    public ExampleAutoIdDAO setValid(boolean valid) {
        isValid = valid;
        return this;
    }
}
