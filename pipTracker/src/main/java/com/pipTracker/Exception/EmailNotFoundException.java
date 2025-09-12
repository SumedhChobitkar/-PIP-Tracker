package com.pipTracker.Exception;

public class EmailNotFoundException extends RuntimeException
{
    public EmailNotFoundException(String message)
    {
        super(message);
    }
}
