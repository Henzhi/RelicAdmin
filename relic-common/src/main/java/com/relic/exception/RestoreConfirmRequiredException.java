package com.relic.exception;

public class RestoreConfirmRequiredException extends RuntimeException {
    public RestoreConfirmRequiredException(String message) {
        super(message);
    }
}
