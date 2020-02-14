package com.yixsoft.support.mybatis.dialects.oracle.mappers;

/**
 * Create by davep at 2020-02-13 15:28
 */
public class OracleColumnDAO {
    private String columnName;
    private String dataType;
    private String nullable;

    public String getColumnName() {
        return columnName;
    }

    public OracleColumnDAO setColumnName(String columnName) {
        this.columnName = columnName;
        return this;
    }

    public String getDataType() {
        return dataType;
    }

    public OracleColumnDAO setDataType(String dataType) {
        this.dataType = dataType;
        return this;
    }

    public String getNullable() {
        return nullable;
    }

    public OracleColumnDAO setNullable(String nullable) {
        this.nullable = nullable;
        return this;
    }
}
