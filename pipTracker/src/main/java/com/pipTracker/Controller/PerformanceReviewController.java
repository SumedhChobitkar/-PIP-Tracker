package com.pipTracker.Controller;

import com.pipTracker.Entity.PerformanceReview;
import com.pipTracker.Service.PerformanceReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/performance-reviews")
public class PerformanceReviewController {

    private final PerformanceReviewService service;

    // === CREATE review with auto reviewer ===
    @PostMapping("/save")
    public ResponseEntity<?> create(@RequestBody PerformanceReview review) {
        PerformanceReview saved = service.saveWithReviewerAuto(review);
        return ResponseEntity.created(
                URI.create("/api/performance-reviews/getById/" + saved.getReviewId())
        ).body(saved);
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/get")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody PerformanceReview incoming) {
        return ResponseEntity.ok(service.update(id, incoming));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok("Review deleted with id " + id);
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<?> getByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(service.getByEmployeeId(employeeId));
    }

    @GetMapping("/reviewer/{reviewerId}")
    public ResponseEntity<?> getByReviewer(@PathVariable Long reviewerId) {
        return ResponseEntity.ok(service.getByReviewerId(reviewerId));
    }

    @PostMapping("/{id}/upload")
    public ResponseEntity<?> uploadFile(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        List<String> allowedTypes = List.of(MediaType.APPLICATION_PDF_VALUE,
                MediaType.IMAGE_JPEG_VALUE,
                "image/jpg");

        if (file.getContentType() == null || !allowedTypes.contains(file.getContentType())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Only PDF, JPG, and JPEG files are allowed.");
        }

        PerformanceReview review = service.uploadFile(id, file);
        return ResponseEntity.ok("File uploaded successfully: " + review.getFileName());
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<?> downloadFile(@PathVariable Long id) {
        PerformanceReview review = service.getById(id);
        byte[] data = service.downloadFile(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(review.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + review.getFileName() + "\"")
                .body(data);
    }
}
