package com.yixsoft.support.mybatis.dialects.mysql.mappers;

/**
 * Create by davep at 2020-02-13 15:16
 */
public class DescMySQLDAO {
    private String columnName;
    private String dataType;
    private String isNullable;

    public String getColumnName() {
        return columnName;
    }

    public DescMySQLDAO setColumnName(String columnName) {
        this.columnName = columnName;
        return this;
    }

    public String getDataType() {
        return dataType;
    }

    public DescMySQLDAO setDataType(String dataType) {
        this.dataType = dataType;
        return this;
    }

    public String getIsNullable() {
        return isNullable;
    }

    public DescMySQLDAO setIsNullable(String isNullable) {
        this.isNullable = isNullable;
        return this;
    }
}
