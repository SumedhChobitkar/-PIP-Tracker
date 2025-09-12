package com.pipTracker.Exception;

public class NotificationNotFoundException extends RuntimeException
{
    public NotificationNotFoundException(String message)
    {
        super(message);
    }
}
