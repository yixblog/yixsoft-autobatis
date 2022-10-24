package com.yixsoft.support.mybatis.test;

import java.util.Date;

/**
 * Create by davep at 2020-02-25 2:40
 */
public class ExampleDAO {
    private String entityId;
    private String groupName;
    private Date createTime;
    private String remark;
    private EntityType type;
    private EntityValueType valueType;
    private Boolean isValid;

    public ExampleDAO() {
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

    public EntityType getType() {
        return type;
    }

    public ExampleDAO setType(EntityType type) {
        this.type = type;
        return this;
    }

    public EntityValueType getValueType() {
        return valueType;
    }

    public ExampleDAO setValueType(EntityValueType valueType) {
        this.valueType = valueType;
        return this;
    }

    public Boolean getValid() {
        return isValid;
    }

    public ExampleDAO setValid(Boolean valid) {
        isValid = valid;
        return this;
    }
}
