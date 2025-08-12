package com.pipTracker.Exception;

public class PipNotFoundException extends RuntimeException {
    public PipNotFoundException(String message) {
        super(message);
    }
}
