package cn.yixblog.support.mybatis.autosql.annotations;

@Deprecated
public enum SqlType {
    SELECT,
    UPDATE,
    DELETE,
    COUNT,
    INSERT;

    public com.yixsoft.support.mybatis.autosql.annotations.SqlType convertSqlType() {
        return com.yixsoft.support.mybatis.autosql.annotations.SqlType.valueOf(this.name());
    }
}
