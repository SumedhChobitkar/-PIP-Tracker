package com.pipTracker.Controller;

import com.pipTracker.Entity.PerformanceReview;
import com.pipTracker.Service.PerformanceReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;

@Tag(
        name = "Performance Review APIs",
        description = "Operations related to creating, updating, deleting, and managing Performance Reviews with PDF/JPG/JPEG upload/download"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/performance-reviews")
public class PerformanceReviewController {

    private final PerformanceReviewService service;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    // Create Performance Review
    @Operation(summary = "Create Performance Review", description = "Creates a performance review with auto reviewer assignment.\n\nEg: POST /save")
    @ApiResponse(responseCode = "201", description = "Performance review created successfully")
    @ApiResponse(responseCode = "400", description = "Failed to create performance review")
    @PostMapping("/save")
    public ResponseEntity<?> create(@RequestBody PerformanceReview review) {
        PerformanceReview saved = service.saveWithReviewerAuto(review);
        return ResponseEntity.created(
                URI.create("/api/performance-reviews/getById/" + saved.getReviewId())
        ).body(saved);
    }

    // Get by ID
    @Operation(summary = "Get Performance Review by ID", description = "Fetch a specific performance review using its ID.\n\nEg: GET /getById/{id}")
    @ApiResponse(responseCode = "200", description = "Performance review fetched successfully")
    @GetMapping("/getById/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // Get all reviews
    @Operation(summary = "Get All Performance Reviews", description = "Fetch all performance reviews.\n\nEg: GET /get")
    @ApiResponse(responseCode = "200", description = "All performance reviews fetched successfully")
    @GetMapping("/get")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // Update review
    @Operation(summary = "Update Performance Review", description = "Update an existing performance review by ID.\n\nEg: PUT /update/{id}")
    @ApiResponse(responseCode = "200", description = "Performance review updated successfully")
    @ApiResponse(responseCode = "400", description = "Update failed")
    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody PerformanceReview incoming) {
        return ResponseEntity.ok(service.update(id, incoming));
    }

    // Delete review
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body("Review deleted with id " + id);
    }


    // Get by employee
    @Operation(summary = "Get Reviews by Employee", description = "Fetch all performance reviews for a specific employee.\n\nEg: GET /employee/{employeeId}")
    @ApiResponse(responseCode = "200", description = "Performance reviews fetched successfully")
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<?> getByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(service.getByEmployeeId(employeeId));
    }

    // Get by reviewer
    @Operation(summary = "Get Reviews by Reviewer", description = "Fetch all performance reviews assigned to a specific reviewer.\n\nEg: GET /reviewer/{reviewerId}")
    @ApiResponse(responseCode = "200", description = "Performance reviews fetched successfully")
    @GetMapping("/reviewer/{reviewerId}")
    public ResponseEntity<?> getByReviewer(@PathVariable Long reviewerId) {
        return ResponseEntity.ok(service.getByReviewerId(reviewerId));
    }

    // Upload file (PDF, JPG, JPEG)
    @Operation(summary = "Upload file for Performance Review", description = "Upload PDF, JPG, or JPEG for a specific performance review.\n\nEg: POST /{id}/upload")
    @ApiResponse(responseCode = "200", description = "File uploaded successfully")
    @ApiResponse(responseCode = "400", description = "Invalid file type or size")
    @PostMapping(value = "/{id}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFile(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            if (file == null || file.isEmpty() || file.getContentType() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please upload a valid file.");
            }

            List<String> allowedTypes = List.of(
                    MediaType.APPLICATION_PDF_VALUE,
                    MediaType.IMAGE_JPEG_VALUE,
                    "image/jpg"
            );

            if (!allowedTypes.contains(file.getContentType())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Only PDF, JPG, and JPEG files are allowed.");
            }

            if (file.getSize() > MAX_FILE_SIZE) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("File size must be <= 5MB");
            }

            PerformanceReview review = service.uploadFile(id, file);
            return ResponseEntity.ok(
                    "File uploaded successfully: " + review.getFileName() +
                            "\nDownload URL: /api/performance-reviews/" + review.getReviewId() + "/download"
            );

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error uploading file: " + e.getMessage());
        }
    }

    // Download file
    @Operation(summary = "Download file from Performance Review", description = "Download uploaded file for a performance review.\n\nEg: GET /{id}/download")
    @ApiResponse(responseCode = "200", description = "File downloaded successfully")
    @GetMapping("/{id}/download")
    public ResponseEntity<?> downloadFile(@PathVariable Long id) {
        try {
            PerformanceReview review = service.getById(id);
            byte[] data = service.downloadFile(id);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(review.getFileType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + review.getFileName() + "\"")
                    .body(data);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error: " + e.getMessage());
        }
    }
}
