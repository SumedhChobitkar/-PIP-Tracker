package com.pipTracker.Exception;

public class PerformanceReviewNotFoundException extends RuntimeException {
  public PerformanceReviewNotFoundException(String message) {
    super(message);
  }
}