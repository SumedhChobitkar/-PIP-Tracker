package com.pipTracker.Controller;

import com.pipTracker.Entity.Employee;
import com.pipTracker.Entity.PerformanceReview;
import com.pipTracker.Entity.ReviewType;
import com.pipTracker.Service.PerformanceReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class PerformanceReviewControllerTest {

    @Mock
    private PerformanceReviewService service;

    @InjectMocks
    private PerformanceReviewController controller;

    private Employee employee;
    private PerformanceReview review;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        employee = Employee.builder()
                .employeeId(1L)
                .name("John Doe")
                .email("john@example.com")
                .build();

        review = PerformanceReview.builder()
                .reviewId(100L)
                .employee(employee)
                .reviewDate(LocalDate.now())
                .reviewPeriod("Q3 2025")
                .scores("{\"quality\":5}")
                .overallRating(4.5)
                .comments("Excellent performance")
                .reviewType(ReviewType.QUARTERLY)
                .build();
    }

    // === Positive: Create Review ===
    @Test
    void testCreateReview_Success() {
        when(service.saveWithReviewerAuto(any(PerformanceReview.class))).thenReturn(review);

        ResponseEntity<?> response = controller.create(review);

        assertEquals(201, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("100"));
        verify(service, times(1)).saveWithReviewerAuto(any());
    }

    // === Negative: Create Review - Invalid Data ===
    @Test
    void testCreateReview_Failure_InvalidReview() {
        review.setReviewPeriod(""); // invalid

        ResponseEntity<?> response = controller.create(review);

        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Review period cannot be empty"));
        verify(service, never()).saveWithReviewerAuto(any());
    }

    // === Positive: Get Review by ID ===
    @Test
    void testGetById_Success() {
        when(service.getById(100L)).thenReturn(review);

        ResponseEntity<?> response = controller.getById(100L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(review, response.getBody());
        verify(service, times(1)).getById(100L);
    }

    // === Positive: Get All Reviews ===
    @Test
    void testGetAll_Success() {
        when(service.getAll()).thenReturn(List.of(review));

        ResponseEntity<?> response = controller.getAll();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, ((List<?>) response.getBody()).size());
        verify(service, times(1)).getAll();
    }

    // === Positive: Update Review ===
    @Test
    void testUpdateReview_Success() {
        PerformanceReview updated = PerformanceReview.builder()
                .reviewId(100L)
                .employee(employee)
                .reviewDate(LocalDate.now())
                .reviewPeriod("Q4 2025") // Changed to simulate update
                .scores("{\"quality\":5}")
                .overallRating(4.8)
                .comments("Updated comment")
                .reviewType(ReviewType.QUARTERLY)
                .build();

        when(service.update(eq(100L), any(PerformanceReview.class))).thenReturn(updated);

        ResponseEntity<?> response = controller.update(100L, updated);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Updated comment", ((PerformanceReview) response.getBody()).getComments());
        assertEquals(4.8, ((PerformanceReview) response.getBody()).getOverallRating());
        verify(service, times(1)).update(eq(100L), any());
    }

    // === Positive: Delete Review ===
    @Test
    void testDeleteReview_Success() {
        doNothing().when(service).delete(100L);

        ResponseEntity<?> response = controller.delete(100L);

        assertEquals(200, response.getStatusCodeValue());
        verify(service, times(1)).delete(100L);
    }

    // === Positive: Upload File ===
    @Test
    void testUploadFile_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.pdf", MediaType.APPLICATION_PDF_VALUE, "dummy content".getBytes()
        );
        when(service.uploadFile(eq(100L), any())).thenReturn(review);

        ResponseEntity<?> response = controller.uploadFile(100L, file);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("File uploaded successfully"));
        verify(service, times(1)).uploadFile(eq(100L), any());
    }

    // === Negative: Upload File - Invalid Type ===
    @Test
    void testUploadFile_Failure_InvalidType() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", MediaType.TEXT_PLAIN_VALUE, "dummy content".getBytes()
        );

        ResponseEntity<?> response = controller.uploadFile(100L, file);

        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Only PDF, JPG, and JPEG files are allowed."));
        verify(service, never()).uploadFile(anyLong(), any());
    }

    // === Positive: Download File ===
    @Test
    void testDownloadFile_Success() {
        review.setFileName("file.pdf");
        review.setFileType(MediaType.APPLICATION_PDF_VALUE);
        review.setFileData("dummy".getBytes());

        when(service.getById(100L)).thenReturn(review);
        when(service.downloadFile(100L)).thenReturn("dummy".getBytes());

        ResponseEntity<?> response = controller.downloadFile(100L);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getHeaders().get("Content-Disposition").get(0).contains("file.pdf"));
        verify(service, times(1)).downloadFile(100L);
    }
}
