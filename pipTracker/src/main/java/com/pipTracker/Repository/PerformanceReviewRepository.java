package com.pipTracker.Repository;

import com.pipTracker.Entity.PerformanceReview;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PerformanceReviewRepository extends JpaRepository<PerformanceReview, Long> {
    List<PerformanceReview> findByEmployee_EmployeeId(Long employeeId);
    List<PerformanceReview> findByReviewer_EmployeeId(Long reviewerId);
}