package com.pipTracker.ServiceImpl;

import com.pipTracker.Entity.*;
import com.pipTracker.Exception.FeedBackNotFoundException;
import com.pipTracker.Repository.EmployeeRepository;
import com.pipTracker.Repository.FeedBackRepository;
import com.pipTracker.Service.AuditLogService;
import com.pipTracker.Service.EmailSenderService;
import com.pipTracker.Service.Notificationservice;
import com.pipTracker.ServiceImpl.FeedBackServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FeedBackImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private FeedBackRepository feedbackRepository;

    @Mock
    private AuditLogService auditLogService;

    @Mock
    private Notificationservice notificationService;

    @Mock
    private EmailSenderService emailSenderService;

    @InjectMocks
    private FeedBackServiceImpl feedBackService;

    private Employee emp;
    private FeedBack feedback;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        emp = new Employee();
        emp.setEmployeeId(1L);
        emp.setName("John");
        emp.setEmail("john@example.com");

        feedback = new FeedBack();
        feedback.setFeedbackId(10L);
        feedback.setToUserId(2L);
        feedback.setFeedbackType(feedbackType.MANAGER);
        feedback.setComments("Well done"); // valid comment for validation
        feedback.setRating(5);
        feedback.setIsAnonymous(false);
    }

    @Test
    void testAddFeedbackToEmployee_Success() {
        Employee toEmp = new Employee();
        toEmp.setEmployeeId(2L);
        toEmp.setName("Jane");
        toEmp.setEmail("jane@example.com");

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(emp));
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(toEmp));
        when(feedbackRepository.save(any(FeedBack.class))).thenReturn(feedback);

        FeedBack saved = feedBackService.addFeedbackToEmployee(1L, feedback);

        assertNotNull(saved);
        assertEquals(10L, saved.getFeedbackId());
        verify(feedbackRepository, times(1)).save(any(FeedBack.class));
        verify(auditLogService, times(1)).createAuditLogFeedBack(any(AuditLog.class));
        verify(notificationService, times(1)).createNotification(any(Notification.class));
        verify(emailSenderService, times(1)).sendEmail(eq("jane@example.com"), anyString(), anyString());
    }

    @Test
    void testUpdateFeedbackByEmployeeId_Success() {
        feedback.setFeedbackId(10L);
        feedback.setEmployee(emp);

        FeedBack updated = new FeedBack();
        updated.setFeedbackId(10L);
        updated.setToUserId(2L);
        updated.setFeedbackType(feedbackType.SELF);
        updated.setComments("Updated comment");
        updated.setRating(4);
        updated.setIsAnonymous(true);
        updated.setEmployee(emp);

        when(feedbackRepository.findById(10L)).thenReturn(Optional.of(feedback));
        when(feedbackRepository.save(any(FeedBack.class))).thenReturn(feedback);

        FeedBack saved = feedBackService.updateFeedbackByEmployeeId(1L, updated);

        assertNotNull(saved);
        assertEquals("Updated comment", saved.getComments());
        assertEquals(4, saved.getRating());
        verify(auditLogService, times(1)).updateAuditlogFeedBack(any(AuditLog.class));
    }

    @Test
    void testGetAllFeedback_Success() {
        FeedBack fb1 = new FeedBack();
        fb1.setFeedbackId(1L);
        fb1.setComments("Good work");

        FeedBack fb2 = new FeedBack();
        fb2.setFeedbackId(2L);
        fb2.setComments("Needs improvement");

        when(feedbackRepository.findAll()).thenReturn(Arrays.asList(fb1, fb2));

        List<FeedBack> result = feedBackService.getAllFeedback();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Good work", result.get(0).getComments());
    }

    @Test
    void testDeleteFeedbackByEmployeeId_Success() {
        feedback.setEmployee(emp);
        when(feedbackRepository.findById(10L)).thenReturn(Optional.of(feedback));

        feedBackService.deleteFeedbackByEmployeeId(1L, 10L);

        verify(feedbackRepository, times(1)).delete(feedback);
        verify(auditLogService, times(1)).createAuditLogFeedBack(any(AuditLog.class));
    }

    @Test
    void testAddFeedbackToEmployee_EmployeeNotFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        FeedBackNotFoundException ex = assertThrows(
                FeedBackNotFoundException.class,
                () -> feedBackService.addFeedbackToEmployee(1L, feedback)
        );

        assertEquals("Employee not found with ID: 1", ex.getMessage());
    }

    @Test
    void testUpdateFeedbackByEmployeeId_EmployeeMismatch() {
        Employee emp2 = new Employee();
        emp2.setEmployeeId(99L);
        feedback.setEmployee(emp2);
        feedback.setFeedbackId(10L);

        when(feedbackRepository.findById(10L)).thenReturn(Optional.of(feedback));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> feedBackService.updateFeedbackByEmployeeId(1L, feedback));

        assertEquals("Feedback does not belong to the specified employee", ex.getMessage());
    }

    @Test
    void testDeleteFeedbackByEmployeeId_NotFound() {
        when(feedbackRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(FeedBackNotFoundException.class,
                () -> feedBackService.deleteFeedbackByEmployeeId(1L, 10L));
    }

    @Test
    void testDeleteFeedbackByEmployeeId_EmployeeMismatch() {
        Employee emp2 = new Employee();
        emp2.setEmployeeId(99L);
        feedback.setEmployee(emp2);
        feedback.setFeedbackId(10L);

        when(feedbackRepository.findById(10L)).thenReturn(Optional.of(feedback));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> feedBackService.deleteFeedbackByEmployeeId(1L, 10L));

        assertEquals("Feedback does not belong to the specified employee", ex.getMessage());
    }
}
