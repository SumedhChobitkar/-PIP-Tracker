package com.pipTracker.serviceimpl;

import com.pipTracker.Entity.*;
import com.pipTracker.Exception.EmployeeNotFoundException;
import com.pipTracker.Exception.PerformanceReviewNotFoundException;
import com.pipTracker.Repository.EmployeeRepository;
import com.pipTracker.Repository.PerformanceReviewRepository;
import com.pipTracker.Repository.UserRepository;
import com.pipTracker.Service.AuditLogService;
import com.pipTracker.Service.EmailSenderService;
import com.pipTracker.Service.Notificationservice;
import com.pipTracker.ServiceImpl.PerformanceReviewServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PerformanceReviewServiceImplTest {

    @InjectMocks
    private PerformanceReviewServiceImpl service;

    @Mock
    private PerformanceReviewRepository repository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private AuditLogService auditLogService;

    @Mock
    private Notificationservice notificationService;

    @Mock
    private EmailSenderService emailSenderService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private PerformanceReview review;
    private Employee employee;
    private Employee reviewer;

    @BeforeEach
    void setup() {
        employee = new Employee();
        employee.setEmployeeId(10L);
        employee.setName("John Doe");
        employee.setEmail("john@example.com");

        reviewer = new Employee();
        reviewer.setEmployeeId(20L);
        reviewer.setName("Reviewer Name");
        reviewer.setEmail("reviewer@example.com");

        review = new PerformanceReview();
        review.setReviewId(1L);
        review.setEmployee(employee);
        review.setReviewer(reviewer); // Set reviewer for validation
        review.setReviewPeriod("Q1 2025");
        review.setReviewDate(LocalDate.now());
        review.setOverallRating(4.5);
        review.setComments("Good performance");
    }

    // ---------- saveWithReviewerAuto ----------
    @Test
    void testSaveWithReviewerAuto_success() {
        User user = new User();
        user.setEmployee(reviewer);

        // Mock SecurityContextHolder
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("reviewer@example.com");
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByEmail("reviewer@example.com")).thenReturn(Optional.of(user));
        when(employeeRepository.findById(reviewer.getEmployeeId())).thenReturn(Optional.of(reviewer));
        when(repository.save(any(PerformanceReview.class))).thenReturn(review);

        PerformanceReview saved = service.saveWithReviewerAuto(review);

        assertNotNull(saved);
        assertEquals(1L, saved.getReviewId());

        verify(auditLogService, times(1)).createAuditLogPerformanceReview(any());
        verify(notificationService, times(1)).createNotification(any());
    }

    // ---------- getById ----------
    @Test
    void testGetById_success() {
        when(repository.findById(1L)).thenReturn(Optional.of(review));
        PerformanceReview result = service.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getReviewId());
    }

    @Test
    void testGetById_notFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(PerformanceReviewNotFoundException.class, () -> service.getById(1L));
    }

    // ---------- update ----------
    @Test
    void testUpdate_success() {
        PerformanceReview incoming = new PerformanceReview();
        incoming.setReviewPeriod("Q2 2025");
        incoming.setReviewDate(LocalDate.now());
        incoming.setOverallRating(5.0);
        incoming.setComments("Excellent");
        incoming.setReviewer(reviewer); // reviewer must be set for validation
        incoming.setEmployee(employee);

        when(repository.findById(1L)).thenReturn(Optional.of(review));
        when(repository.save(any())).thenReturn(incoming);

        PerformanceReview updated = service.update(1L, incoming);

        assertNotNull(updated);
        assertEquals("Q2 2025", updated.getReviewPeriod());
        assertEquals(5.0, updated.getOverallRating());
    }

    @Test
    void testUpdate_notFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(PerformanceReviewNotFoundException.class, () -> service.update(1L, review));
    }

    // ---------- delete ----------
    @Test
    void testDelete_success() {
        when(repository.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void testDelete_notFound() {
        when(repository.existsById(1L)).thenReturn(false);
        assertThrows(PerformanceReviewNotFoundException.class, () -> service.delete(1L));
    }

    // ---------- file upload/download ----------
    @Test
    void testUploadFile_success() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "review.pdf", "application/pdf", "PDF content".getBytes());

        when(repository.findById(1L)).thenReturn(Optional.of(review));
        when(repository.save(any())).thenReturn(review);

        PerformanceReview saved = service.uploadFile(1L, file);

        assertNotNull(saved);
        assertEquals("review.pdf", saved.getFileName());
    }

    @Test
    void testDownloadFile_success() {
        review.setFileData("PDF content".getBytes());
        when(repository.findById(1L)).thenReturn(Optional.of(review));

        byte[] data = service.downloadFile(1L);

        assertNotNull(data);
        assertEquals("PDF content", new String(data));
    }

    @Test
    void testDownloadFile_notFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.downloadFile(1L));
    }

    @Test
    void testDownloadFile_noFile() {
        review.setFileData(null);
        when(repository.findById(1L)).thenReturn(Optional.of(review));

        assertThrows(RuntimeException.class, () -> service.downloadFile(1L));
    }
}
