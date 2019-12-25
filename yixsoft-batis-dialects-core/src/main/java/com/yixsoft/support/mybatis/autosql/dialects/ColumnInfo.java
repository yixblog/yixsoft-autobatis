package com.yixsoft.support.mybatis.autosql.dialects;

/**
 * column info
 * Created by yixian on 2015-08-31.
 */
public class ColumnInfo {
    private String column;
    private String jdbcType;
    private Boolean allowNull;

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getJdbcType() {
        return jdbcType;
    }

    public void setJdbcType(String jdbcType) {
        this.jdbcType = jdbcType;
    }

    public Boolean getAllowNull() {
        return allowNull;
    }

    public void setAllowNull(Boolean allowNull) {
        this.allowNull = allowNull;
    }
}
