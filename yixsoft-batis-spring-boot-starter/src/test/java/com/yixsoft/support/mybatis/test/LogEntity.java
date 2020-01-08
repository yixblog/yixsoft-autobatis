package com.yixsoft.support.mybatis.test;

/**
 * Create by davep at 2020-01-08 17:30
 */
public class LogEntity {
    private String id;
    private String content;
    private int userid;

    public String getId() {
        return id;
    }

    public LogEntity setId(String id) {
        this.id = id;
        return this;
    }

    public String getContent() {
        return content;
    }

    public LogEntity setContent(String content) {
        this.content = content;
        return this;
    }

    public int getUserid() {
        return userid;
    }

    public LogEntity setUserid(int userid) {
        this.userid = userid;
        return this;
    }
}
