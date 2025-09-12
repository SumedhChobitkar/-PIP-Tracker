package com.pipTracker.Exception;

public class AuditLogNotFoundException extends RuntimeException
{
    public AuditLogNotFoundException(String message) {
        super(message);
    }
}
