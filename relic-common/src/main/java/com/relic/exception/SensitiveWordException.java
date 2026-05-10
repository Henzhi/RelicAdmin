package com.relic.exception;

public class SensitiveWordException extends RuntimeException {
    public SensitiveWordException(String message) {
        super(message);
    }
}
