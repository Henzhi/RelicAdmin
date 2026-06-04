package com.relic.exception;

/**
 * 账号被锁定异常
 */
public class InsufficientPermissionsException extends BaseException {

    public InsufficientPermissionsException() {
    }

    public InsufficientPermissionsException(String msg) {
        super(msg);
    }

}
