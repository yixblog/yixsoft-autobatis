package com.yixsoft.support.mybatis.exceptions;

public class YixBatisException extends RuntimeException{
    public YixBatisException() {
    }

    public YixBatisException(String message) {
        super(message);
    }

    public YixBatisException(String message, Throwable cause) {
        super(message, cause);
    }

    public YixBatisException(Throwable cause) {
        super(cause);
    }

    public YixBatisException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
