package com.pipTracker.Controller;

import com.pipTracker.Entity.PerformanceReview;
import com.pipTracker.Service.PerformanceReviewService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/performance")
public class PerformanceReviewController {

    private final PerformanceReviewService service;

    public PerformanceReviewController(PerformanceReviewService service) {
        this.service = service;
    }

    // === Create Review ===
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody PerformanceReview review) {
        // Simple validation
        if (review.getReviewPeriod() == null || review.getReviewPeriod().isEmpty()) {
            return ResponseEntity.badRequest().body("Review period cannot be empty");
        }
        PerformanceReview saved = service.saveWithReviewerAuto(review);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // === Get By ID ===
    @GetMapping("/getById/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            PerformanceReview review = service.getById(id);
            return ResponseEntity.ok(review);
        } catch (RuntimeException e) { // catch not found exception
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // === Get All ===
    @GetMapping("/get")
    public ResponseEntity<?> getAll() {
        List<PerformanceReview> reviews = service.getAll();
        return ResponseEntity.ok(reviews);
    }

    // === Update ===
    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody PerformanceReview review) {
        try {
            PerformanceReview updated = service.update(id, review);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // === Delete ===
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            service.delete(id);
            return ResponseEntity.ok("Performance review deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // === Upload File ===
    @PostMapping("/upload/{id}")
    public ResponseEntity<?> uploadFile(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null ||
                !(contentType.equals(MediaType.APPLICATION_PDF_VALUE)
                        || contentType.equals(MediaType.IMAGE_JPEG_VALUE)
                        || contentType.equals(MediaType.IMAGE_JPEG_VALUE.replace("image/jpeg","image/jpg")))) {
            return ResponseEntity.badRequest()
                    .body("Only PDF, JPG, and JPEG files are allowed.");
        }

        try {
            service.uploadFile(id, file);
            return ResponseEntity.ok("File uploaded successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // === Download File ===
    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long id) {
        try {
            PerformanceReview review = service.getById(id);
            byte[] data = service.downloadFile(id);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(review.getFileType()));
            headers.setContentDispositionFormData("attachment", review.getFileName());

            return new ResponseEntity<>(data, headers, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
}
