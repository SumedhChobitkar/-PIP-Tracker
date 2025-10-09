package com.pipTracker.ServiceImpl;

import com.pipTracker.CommonUtil.Validation;
import com.pipTracker.Entity.*;
import com.pipTracker.Exception.EmployeeNotFoundException;
import com.pipTracker.Exception.PerformanceReviewNotFoundException;
import com.pipTracker.Repository.EmployeeRepository;
import com.pipTracker.Repository.PerformanceReviewRepository;
import com.pipTracker.Repository.UserRepository;
import com.pipTracker.Service.AuditLogService;
import com.pipTracker.Service.EmailSenderService;
import com.pipTracker.Service.Notificationservice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class PerformanceReviewServiceImplTest {

    @Mock private PerformanceReviewRepository repository;
    @Mock private UserRepository userRepository;
    @Mock private EmployeeRepository employeeRepository;
    @Mock private AuditLogService auditLogService;
    @Mock private Notificationservice notificationService;
    @Mock private EmailSenderService emailSenderService;

    @InjectMocks
    private PerformanceReviewServiceImpl service;

    private Employee emp;
    private Employee reviewer;
    private PerformanceReview review;

    @BeforeEach
    void setUp() {
        emp = Employee.builder().employeeId(1L).name("Test Emp").email("emp@test.com").build();
        reviewer = Employee.builder().employeeId(2L).name("Reviewer").email("rev@test.com").build();

        review = PerformanceReview.builder()
                .reviewId(1L)
                .employee(emp)
                .reviewer(reviewer)
                .reviewPeriod("2025-Q1")
                .reviewDate(LocalDate.now().minusDays(1))
                .overallRating(4.0)
                .comments("Good")
                .build();
    }

    @Test
    void testSave_Success() {
        try (MockedStatic<Validation> validationMock = mockStatic(Validation.class)) {
            validationMock.when(() -> Validation.validatePerformanceReview(any(PerformanceReview.class), eq(false)))
                    .thenAnswer(inv -> null);

            when(repository.save(any())).thenReturn(review);

            PerformanceReview saved = service.save(review);

            assertNotNull(saved);
            assertEquals(1L, saved.getReviewId());
            verify(repository).save(review);
        }
    }

    @Test
    void testGetById_Success() {
        when(repository.findById(1L)).thenReturn(Optional.of(review));

        PerformanceReview found = service.getById(1L);

        assertEquals(1L, found.getReviewId());
        verify(repository).findById(1L);
    }

    @Test
    void testGetById_NotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(PerformanceReviewNotFoundException.class, () -> service.getById(99L));
    }

    @Test
    void testUpdate_Success() {
        PerformanceReview updated = PerformanceReview.builder()
                .reviewId(1L).employee(emp).reviewer(reviewer)
                .reviewPeriod("2025-Q2").reviewDate(LocalDate.now().minusDays(2))
                .overallRating(5.0).comments("Updated").build();

        when(repository.findById(1L)).thenReturn(Optional.of(review));
        when(repository.save(any())).thenReturn(updated);

        try (MockedStatic<Validation> validationMock = mockStatic(Validation.class)) {
            validationMock.when(() -> Validation.validatePerformanceReview(any(), eq(true)))
                    .thenAnswer(inv -> null);

            PerformanceReview result = service.update(1L, updated);

            assertEquals("Updated", result.getComments());
            verify(repository).save(review);
        }
    }

    @Test
    void testDelete_Success() {
        when(repository.existsById(1L)).thenReturn(true);
        service.delete(1L);
        verify(repository).deleteById(1L);
    }

    @Test
    void testDelete_NotFound() {
        when(repository.existsById(1L)).thenReturn(false);
        assertThrows(PerformanceReviewNotFoundException.class, () -> service.delete(1L));
    }

    @Test
    void testGetAll() {
        when(repository.findAll()).thenReturn(List.of(review));
        List<PerformanceReview> list = service.getAll();
        assertEquals(1, list.size());
    }

    @Test
    void testGetByEmployeeId() {
        when(repository.findByEmployee_EmployeeId(1L)).thenReturn(List.of(review));
        List<PerformanceReview> list = service.getByEmployeeId(1L);
        assertEquals(1, list.size());
    }

    @Test
    void testGetByReviewerId() {
        when(repository.findByReviewer_EmployeeId(2L)).thenReturn(List.of(review));
        List<PerformanceReview> list = service.getByReviewerId(2L);
        assertEquals(1, list.size());
    }

    @Test
    void testUploadFile_Success() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("file.pdf");
        when(file.getContentType()).thenReturn("application/pdf");
        when(file.getBytes()).thenReturn("dummy".getBytes());

        when(repository.findById(1L)).thenReturn(Optional.of(review));
        when(repository.save(any())).thenReturn(review);

        try (MockedStatic<Validation> validationMock = mockStatic(Validation.class)) {
            validationMock.when(() -> Validation.validatePerformanceReview(any(), eq(true)))
                    .thenAnswer(inv -> null);

            PerformanceReview result = service.uploadFile(1L, file);
            assertEquals("file.pdf", result.getFileName());
        }
    }

    @Test
    void testDownloadFile_Success() {
        review.setFileData("abc".getBytes());
        when(repository.findById(1L)).thenReturn(Optional.of(review));
        byte[] data = service.downloadFile(1L);
        assertNotNull(data);
    }

    @Test
    void testDownloadFile_NoFile() {
        review.setFileData(null);
        when(repository.findById(1L)).thenReturn(Optional.of(review));
        assertThrows(RuntimeException.class, () -> service.downloadFile(1L));
    }

    @Test
    void testSaveWithReviewerAuto_Success() {
        // Mock security context
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("rev@test.com");

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        User user = User.builder().employee(reviewer).email("rev@test.com").build();
        when(userRepository.findByEmail("rev@test.com")).thenReturn(Optional.of(user));
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(reviewer));

        when(repository.save(any())).thenReturn(review);

        try (MockedStatic<Validation> validationMock = mockStatic(Validation.class)) {
            validationMock.when(() -> Validation.validatePerformanceReview(any(), eq(false)))
                    .thenAnswer(inv -> null);

            PerformanceReview saved = service.saveWithReviewerAuto(review);

            assertNotNull(saved);
            verify(auditLogService).createAuditLogPerformanceReview(any());
            verify(notificationService).createNotification(any());
            verify(emailSenderService).sendEmail(eq("rev@test.com"), any(), any());
        }
    }
}
