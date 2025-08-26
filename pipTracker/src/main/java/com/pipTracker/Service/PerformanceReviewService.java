package com.pipTracker.Service;

import com.pipTracker.Entity.PerformanceReview;
import java.util.List;

public interface PerformanceReviewService {
    PerformanceReview save(PerformanceReview review);
    PerformanceReview getById(Long id);
    List<PerformanceReview> getAll();
    PerformanceReview update(Long id, PerformanceReview incoming);
    void delete(Long id);
    List<PerformanceReview> getByEmployeeId(Long employeeId);
    List<PerformanceReview> getByReviewerId(Long reviewerId);
}