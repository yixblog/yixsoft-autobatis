package com.yixsoft.support.mybatis.autosql.dialects.exceptions;

/**
 * auto sql exception
 * Created by yixian on 2015-09-05.
 */
public class AutoSqlException extends RuntimeException {
    public AutoSqlException(String message) {
        super(message);
    }

    public AutoSqlException(String message, Throwable cause) {
        super(message, cause);
    }
}
