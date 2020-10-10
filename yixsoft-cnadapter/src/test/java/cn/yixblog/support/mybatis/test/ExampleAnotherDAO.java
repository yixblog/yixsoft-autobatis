package cn.yixblog.support.mybatis.test;

import com.yixsoft.support.mybatis.autosql.annotations.Column;

import java.util.Date;

/**
 * Create by davep at 2020-02-25 2:40
 */
public class ExampleAnotherDAO {
    private String entityId;
    @Column("group_name")
    private String gname;
    @Column("create_time")
    private Date time;
    private String remark;
    private boolean isValid;

    public ExampleAnotherDAO() {
    }

    public String getEntityId() {
        return entityId;
    }

    public ExampleAnotherDAO setEntityId(String entityId) {
        this.entityId = entityId;
        return this;
    }

    public String getGname() {
        return gname;
    }

    public ExampleAnotherDAO setGname(String gname) {
        this.gname = gname;
        return this;
    }


    public String getRemark() {
        return remark;
    }

    public ExampleAnotherDAO setRemark(String remark) {
        this.remark = remark;
        return this;
    }

    public boolean getValid() {
        return isValid;
    }

    public ExampleAnotherDAO setValid(boolean valid) {
        isValid = valid;
        return this;
    }

    public Date getTime() {
        return time;
    }

    public ExampleAnotherDAO setTime(Date time) {
        this.time = time;
        return this;
    }
}
