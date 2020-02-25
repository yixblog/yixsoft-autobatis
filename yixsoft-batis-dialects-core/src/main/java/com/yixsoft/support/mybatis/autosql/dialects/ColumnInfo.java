package com.yixsoft.support.mybatis.autosql.dialects;

import java.sql.JDBCType;

/**
 * column info
 * Created by yixian on 2015-08-31.
 */
public class ColumnInfo {
    private String column;
    private JDBCType jdbcType;
    private Boolean allowNull;

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public JDBCType getJdbcType() {
        return jdbcType;
    }

    public void setJdbcType(JDBCType jdbcType) {
        this.jdbcType = jdbcType;
    }

    public Boolean getAllowNull() {
        return allowNull;
    }

    public void setAllowNull(Boolean allowNull) {
        this.allowNull = allowNull;
    }
}
