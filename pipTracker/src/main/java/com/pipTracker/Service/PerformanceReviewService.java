package com.pipTracker.Service;

import com.pipTracker.Entity.PerformanceReview;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PerformanceReviewService {

    // === Save Review with reviewer auto-set from logged-in user ===
    PerformanceReview saveWithReviewerAuto(PerformanceReview review);

    PerformanceReview save(PerformanceReview review);

    PerformanceReview saveWithReviewer(PerformanceReview review, Long reviewerId);

    PerformanceReview getById(Long id);

    List<PerformanceReview> getAll();

    PerformanceReview update(Long id, PerformanceReview incoming);

    void delete(Long id);

    List<PerformanceReview> getByEmployeeId(Long employeeId);

    List<PerformanceReview> getByReviewerId(Long reviewerId);

    PerformanceReview uploadFile(Long id, MultipartFile file);

    byte[] downloadFile(Long id);
}
