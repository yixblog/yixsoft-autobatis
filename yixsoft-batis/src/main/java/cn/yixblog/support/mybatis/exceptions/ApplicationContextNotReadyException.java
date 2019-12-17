package cn.yixblog.support.mybatis.exceptions;

/**
 *
 * Created by yixian on 2015-09-02.
 */
public class ApplicationContextNotReadyException extends RuntimeException {
    public ApplicationContextNotReadyException() {
        super("Too Early Calling this method,ApplicationContext not bean initialized");
    }
}
