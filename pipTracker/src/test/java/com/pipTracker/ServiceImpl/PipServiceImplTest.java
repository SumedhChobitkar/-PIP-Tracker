package com.pipTracker.ServiceImpl;

import com.pipTracker.Entity.Employee;
import com.pipTracker.Entity.Pip;
import com.pipTracker.Entity.Status;
import com.pipTracker.Exception.EmployeeNotFoundException;
import com.pipTracker.Exception.PipNotFoundException;
import com.pipTracker.Repository.EmployeeRepository;
import com.pipTracker.Repository.PipRepository;
import com.pipTracker.Service.AuditLogService;
import com.pipTracker.Service.EmailSenderService;
import com.pipTracker.Service.Notificationservice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PipServiceImplTest {

    @InjectMocks
    private PipServiceImpl pipService;   // service under test

    @Mock
    private PipRepository pipRepository;

    @Mock
    private AuditLogService auditLogService;

    @Mock
    private Notificationservice notificationService;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EmailSenderService emailSenderService;

    private Pip pip;

    @BeforeEach
    void setup() {
        pip = new Pip();
        pip.setPipId(1L);
        pip.setReviewerId(100L);
        pip.setGoals("Improve skills");
        pip.setStatus(Status.ACTIVE);
        pip.setStartDate(LocalDate.of(2024, 1, 1));
        pip.setEndDate(LocalDate.of(2024, 2, 1));

        Employee emp = new Employee();
        emp.setEmployeeId(10L);
        emp.setName("Test Emp");
        emp.setEmail("test@example.com");
        pip.setEmployee(emp);
    }

    // ---------- createPip ----------
    @Test
    void testCreatePip_success() {
        when(pipRepository.save(any(Pip.class))).thenReturn(pip);
        when(employeeRepository.findById(100L)).thenReturn(Optional.of(new Employee()));

        Pip saved = pipService.createPip(pip);

        assertNotNull(saved);
        assertEquals(1L, saved.getPipId());

        verify(pipRepository, times(1)).save(pip);
        verify(auditLogService, times(1)).createAuditLogPip(any());
        verify(notificationService, times(1)).createNotification(any());
        verify(emailSenderService, times(1)).sendEmail(any(), any(), any());
    }

    @Test
    void testCreatePip_reviewerNotFound() {
        when(pipRepository.save(any(Pip.class))).thenReturn(pip);
        when(employeeRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> pipService.createPip(pip));
    }


    @Test
    void testCreatePip_nullReviewer() {
        pip.setReviewerId(null);

        when(pipRepository.save(any(Pip.class))).thenReturn(pip);

        assertThrows(RuntimeException.class, () -> pipService.createPip(pip));
    }


    // ---------- getPipById ----------
    @Test
    void testGetPipById_success() {
        when(pipRepository.findById(1L)).thenReturn(Optional.of(pip));

        Pip found = pipService.getPipById(1L);

        assertNotNull(found);
        assertEquals(1L, found.getPipId());
    }

    @Test
    void testGetPipById_notFound() {
        when(pipRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PipNotFoundException.class, () -> pipService.getPipById(1L));
    }

    // ---------- updatePip ----------
    @Test
    void testUpdatePip_success() {
        Pip updatedDetails = new Pip();
        updatedDetails.setGoals("Updated goals");
        updatedDetails.setReviewerId(100L);
        updatedDetails.setStatus(Status.COMPLETED);
        updatedDetails.setStartDate(LocalDate.of(2024, 1, 1));
        updatedDetails.setEndDate(LocalDate.of(2024, 2, 1));
        updatedDetails.setEmployee(pip.getEmployee());

        when(pipRepository.findById(1L)).thenReturn(Optional.of(pip));
        when(pipRepository.save(any(Pip.class))).thenReturn(updatedDetails);
        when(employeeRepository.findById(100L)).thenReturn(Optional.of(new Employee()));

        Pip updated = pipService.updatePip(1L, updatedDetails);

        assertNotNull(updated);
        assertEquals("Updated goals", updated.getGoals());

        verify(pipRepository, times(1)).save(any(Pip.class));
        verify(auditLogService, times(1)).updateAuditlogPip(any());
        verify(notificationService, times(1)).updatePip(any());
    }

    @Test
    void testUpdatePip_notFound() {
        when(pipRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PipNotFoundException.class, () -> pipService.updatePip(1L, pip));
    }

    // ---------- deletePip ----------
    @Test
    void testDeletePip_success() {
        when(pipRepository.findById(1L)).thenReturn(Optional.of(pip));

        pipService.deletePip(1L);

        verify(pipRepository, times(1)).delete(pip);
        verify(auditLogService, times(1)).createAuditLogPip(any());
    }

    @Test
    void testDeletePip_notFound() {
        when(pipRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PipNotFoundException.class, () -> pipService.deletePip(1L));
    }
}
