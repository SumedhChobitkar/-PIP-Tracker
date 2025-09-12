package com.pipTracker.Exception;

public class AuditLogArchieveNotFoundException extends RuntimeException
{
    public AuditLogArchieveNotFoundException(String message)
    {
        super(message);
    }
}
