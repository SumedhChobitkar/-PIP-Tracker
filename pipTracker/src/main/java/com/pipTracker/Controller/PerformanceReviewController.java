package com.pipTracker.Controller;

import com.pipTracker.Entity.PerformanceReview;
import com.pipTracker.Service.PerformanceReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/performance-reviews")
public class PerformanceReviewController {

    private final PerformanceReviewService service;

    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody PerformanceReview review) {
        try {
            PerformanceReview saved = service.save(review);
            return ResponseEntity.created(
                    URI.create("/api/performance-reviews/getById/" + saved.getReviewId())
            ).body(saved);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while saving review: " + e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestBody PerformanceReview incoming) {
        try {
            PerformanceReview updated = service.update(id, incoming);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error while updating review: " + e.getMessage());
        }
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            PerformanceReview review = service.getById(id);
            return ResponseEntity.ok(review);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Review not found: " + e.getMessage());
        }
    }

    @GetMapping("/get")
    public ResponseEntity<?> getAll() {
        try {
            List<PerformanceReview> reviews = service.getAll();
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while fetching reviews: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            service.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error while deleting review: " + e.getMessage());
        }
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<?> byEmployee(@PathVariable Long employeeId) {
        try {
            List<PerformanceReview> reviews = service.getByEmployeeId(employeeId);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error fetching reviews for employee: " + e.getMessage());
        }
    }

    @GetMapping("/reviewer/{reviewerId}")
    public ResponseEntity<?> byReviewer(@PathVariable Long reviewerId) {
        try {
            List<PerformanceReview> reviews = service.getByReviewerId(reviewerId);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error fetching reviews by reviewer: " + e.getMessage());
  }
}
}
