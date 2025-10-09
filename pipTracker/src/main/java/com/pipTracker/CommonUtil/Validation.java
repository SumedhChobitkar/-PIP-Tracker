package com.pipTracker.CommonUtil;

import com.pipTracker.Entity.PerformanceReview;
import java.time.LocalDate;
import java.util.Objects;
import java.util.regex.Pattern;

public class Validation {

    // Rating pattern 0.0 to 5.0
    public static final Pattern RATING_PATTERN =
            Pattern.compile("^([0-4](\\.\\d)?|5(\\.0)?)$");

    // Allowed image/file formats
    public static final Pattern IMAGE_PATTERN =
            Pattern.compile("^.+\\.(?i)(jpg|jpeg|png|gif|bmp|webp|pdf)$");

    /**
      Validate a PerformanceReview object
      @param review   PerformanceReview instance to validate
      @param isUpdate true if validation is for update, false for create
     */
    public static void validatePerformanceReview(PerformanceReview review, boolean isUpdate) {
        if (review == null) {
            throw new IllegalArgumentException("Performance review cannot be null");
        }

        // Employee check
        if (review.getEmployee() == null || review.getEmployee().getEmployeeId() == null) {
            throw new IllegalArgumentException("Employee details are required");
        }

        // Reviewer check (only on update)
        if (isUpdate) {
            if (review.getReviewer() == null || review.getReviewer().getEmployeeId() == null) {
                throw new IllegalArgumentException("Reviewer details are required for update");
            }
        }

        // Review Period
        if (review.getReviewPeriod() == null || review.getReviewPeriod().isBlank()) {
            throw new IllegalArgumentException("Review period cannot be empty");
        }

        // Review Date
        if (review.getReviewDate() == null || review.getReviewDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Review date cannot be in the future or empty");
        }

        // Rating validation
        if (review.getOverallRating() != null) {
            String ratingStr = String.valueOf(review.getOverallRating());
            if (!RATING_PATTERN.matcher(ratingStr).matches()) {
                throw new IllegalArgumentException("Overall rating must be between 0.0 and 5.0");
            }
        }

        // Comments validation
        if (review.getComments() != null && review.getComments().length() > 1000) {
            throw new IllegalArgumentException("Comments cannot exceed 1000 characters");
        }

        // File validation
        if (review.getFileName() != null && !review.getFileName().isBlank()) {
            if (!IMAGE_PATTERN.matcher(review.getFileName()).matches()) {
                throw new IllegalArgumentException(
                        "Invalid file format. Allowed: jpg, jpeg, png, gif, bmp, webp, pdf");
            }

            if (Objects.isNull(review.getFileData()) || review.getFileData().length == 0) {
                throw new IllegalArgumentException("File data is missing for uploaded file");
            }
        }
    }
}
