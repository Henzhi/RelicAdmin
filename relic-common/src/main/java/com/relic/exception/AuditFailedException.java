package com.relic.exception;

public class AuditFailedException extends RuntimeException {
    public AuditFailedException(String message) {
        super(message);
    }
}
