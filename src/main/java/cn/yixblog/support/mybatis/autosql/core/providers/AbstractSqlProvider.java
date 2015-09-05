package cn.yixblog.support.mybatis.autosql.core.providers;

import cn.yixblog.support.mybatis.autosql.core.IAutoSqlProvider;
import cn.yixblog.support.mybatis.autosql.dialects.ColumnInfo;
import cn.yixblog.support.mybatis.autosql.dialects.ISqlDialect;

import java.util.HashMap;
import java.util.Map;

/**
 * global methods here
 * Created by yixian on 2015-09-05.
 */
public abstract class AbstractSqlProvider implements IAutoSqlProvider {
    private ISqlDialect dialect;
    private String[] pkNames;
    private Object param;
    private Map<String, ColumnInfo> tableColumnMap;
    private Map<String, Object> additionalParam = new HashMap<>();
    private String tableName;

    @Override
    public void setParameter(Object parameterObject) {
        this.param = parameterObject;
    }

    @Override
    public void setTableColumns(Map<String, ColumnInfo> tableColumns) {
        this.tableColumnMap = tableColumns;
    }

    @Override
    public void setDialect(ISqlDialect dialect) {
        this.dialect = dialect;
    }

    @Override
    public void setPkNames(String[] pkNames) {
        this.pkNames = pkNames;
    }

    @Override
    public void setTable(String tableName) {
        this.tableName = tableName;
    }

    protected String buildWhereClause(){
        return "";
    }

    protected ISqlDialect getDialect() {
        return dialect;
    }

    protected String[] getPkNames() {
        return pkNames;
    }

    protected Object getParam() {
        return param;
    }

    protected Map<String, ColumnInfo> getTableColumnMap() {
        return tableColumnMap;
    }

    protected Map<String, Object> getAdditionalParam() {
        return additionalParam;
    }

    protected String getTableName() {
        return tableName;
    }
}
