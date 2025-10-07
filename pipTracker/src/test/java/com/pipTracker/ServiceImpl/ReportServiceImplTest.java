package com.pipTracker.ServiceImpl;

import com.pipTracker.Entity.*;
import com.pipTracker.Exception.EmployeeNotFoundException;
import com.pipTracker.Exception.ReportNotFoundException;
import com.pipTracker.Repository.EmployeeRepository;
import com.pipTracker.Repository.ReportRepository;
import com.pipTracker.Service.AuditLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ReportServiceImplTest {

    @InjectMocks
    private ReportServiceImpl reportService;  // inject mocks automatically

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private AuditLogService auditLogService;

    private Employee mockEmployee;
    private Report mockReport;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this); // initialize mocks

        mockEmployee = new Employee();
        mockEmployee.setEmployeeId(1L);
        mockEmployee.setName("Test Employee");

        mockReport = new Report();
        mockReport.setReportId(100L);
        mockReport.setCreatedBy(1L);
        mockReport.setEmployee(mockEmployee);
        mockReport.setGeneratedOn(LocalDateTime.now());
    }

    @Test
    void testCreateReport_WithFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "image.png", "image/png", "data".getBytes());

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(mockEmployee));
        when(reportRepository.save(any(Report.class))).thenReturn(mockReport);

        Report saved = reportService.createReport(mockReport, 1L, file);

        assertNotNull(saved);
        assertEquals(mockEmployee, saved.getEmployee());
        verify(auditLogService, times(1)).createAuditLogFeedBack(any());
    }

    @Test
    void testGetAllReports() {
        when(reportRepository.findAll()).thenReturn(Collections.singletonList(mockReport));

        List<Report> reports = reportService.getAllReports();
        assertEquals(1, reports.size());
    }

    @Test
    void testGetReportById_Found() {
        when(reportRepository.findById(100L)).thenReturn(Optional.of(mockReport));

        Report report = reportService.getReportById(100L);
        assertEquals(100L, report.getReportId());
    }

    @Test
    void testGetReportById_NotFound() {
        when(reportRepository.findById(101L)).thenReturn(Optional.empty());

       // assertThrows(ReportNotFoundException.class, () -> reportService.getReportById(101L));
        assertThrows(RuntimeException.class, () -> reportService.getReportById(101L));

    }

    @Test
    void testGetReportsByEmployeeId() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(mockEmployee));
        when(reportRepository.findByEmployee_EmployeeId(1L)).thenReturn(Collections.singletonList(mockReport));

        List<Report> reports = reportService.getReportsByEmployeeId(1L);
        assertEquals(1, reports.size());
    }

    @Test
    void testGetEmployeeImage() {
        mockReport.setFile("image".getBytes());
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(mockEmployee));
        when(reportRepository.findByEmployee_EmployeeId(1L)).thenReturn(Collections.singletonList(mockReport));

        byte[] image = reportService.getEmployeeImage(1L, "Test Employee");
        assertArrayEquals("image".getBytes(), image);
    }

    @Test
    void testUpdateReport() {
        Report updated = new Report();
        updated.setCreatedBy(2L);
        updated.setReportType(ReportType.PIP);


        when(reportRepository.findById(100L)).thenReturn(Optional.of(mockReport));
        when(reportRepository.save(any(Report.class))).thenReturn(mockReport);

        Report result = reportService.updateReport(100L, updated);

        assertEquals(2L, result.getCreatedBy());
        verify(auditLogService, times(1)).updateAuditlogFeedBack(any());
    }

    @Test
    void testUpdateReportImage() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "image.png", "image/png", "data".getBytes());
        when(reportRepository.findById(100L)).thenReturn(Optional.of(mockReport));
        when(reportRepository.save(any(Report.class))).thenReturn(mockReport);

        Report result = reportService.updateReportImage(100L, file);
        assertNotNull(result.getFile());
    }

    @Test
    void testUpdateImageByEmployeeId() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "image.png", "image/png", "data".getBytes());
        when(reportRepository.findByEmployee_EmployeeId(1L)).thenReturn(Collections.singletonList(mockReport));
        when(reportRepository.save(any(Report.class))).thenReturn(mockReport);

        Report result = reportService.updateImageByEmployeeId(1L, file);
        assertNotNull(result.getFile());
    }

    @Test
    void testDeleteReport() {
        doNothing().when(reportRepository).deleteById(100L);
        boolean deleted = reportService.deleteReport(100L);

        assertTrue(deleted);
        verify(auditLogService, times(1)).createAuditLogFeedBack(any());
    }

    @Test
    void testCreateReport_EmployeeNotFound() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "image.png", "image/png", "data".getBytes());
        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reportService.createReport(mockReport, 999L, file);
        });

        assertTrue(exception.getMessage().contains("Employee not found"));
    }

    @Test
    void testCreateReport_InvalidFileType() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "file.txt", "text/plain", "data".getBytes());
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(mockEmployee));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reportService.createReport(mockReport, 1L, file);
        });

        assertTrue(exception.getMessage().contains("Invalid file format"));
    }

    @Test
    void testGetReportsByEmployeeId_EmployeeNotFound() {
        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

        EmployeeNotFoundException exception = assertThrows(EmployeeNotFoundException.class, () -> {
            reportService.getReportsByEmployeeId(999L);
        });

        assertTrue(exception.getMessage().contains("Employee not found"));
    }

    @Test
    void testGetEmployeeImage_EmployeeIdAndNameMismatch() {
        mockReport.setFile("image".getBytes());
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(mockEmployee));
        when(reportRepository.findByEmployee_EmployeeId(1L)).thenReturn(Collections.singletonList(mockReport));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reportService.getEmployeeImage(1L, "Wrong Name");
        });

        assertTrue(exception.getMessage().contains("EmployeeId and Name do not match"));
    }

    @Test
    void testGetEmployeeImage_NoEmployeeIdOrName() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reportService.getEmployeeImage(null, null);
        });

        assertTrue(exception.getMessage().contains("Please provide either EmployeeId or Name"));
    }

    @Test
    void testGetEmployeeImage_ImageNotFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(mockEmployee));
        when(reportRepository.findByEmployee_EmployeeId(1L)).thenReturn(Collections.emptyList());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reportService.getEmployeeImage(1L, "Test Employee");
        });

        assertTrue(exception.getMessage().contains("Image not found"));
    }

    @Test
    void testUpdateReport_ReportNotFound() {
        when(reportRepository.findById(999L)).thenReturn(Optional.empty());

        Report updated = new Report();
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reportService.updateReport(999L, updated);
        });

        assertFalse(exception.getMessage().contains("Report not found"));
    }

    @Test
    void testUpdateReportImage_ReportNotFound() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "image.png", "image/png", "data".getBytes());

        // Mock repository to return empty Optional
        when(reportRepository.findById(999L)).thenReturn(Optional.empty());

        // Assert exception
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                reportService.updateReportImage(999L, file)
        );

        // Use exact message
        assertEquals("Report not found with ID: 999", exception.getMessage());
    }




    @Test
    void testUpdateImageByEmployeeId_NoReportsFound() {
        MockMultipartFile file = new MockMultipartFile("file", "image.png", "image/png", "data".getBytes());
        when(reportRepository.findByEmployee_EmployeeId(999L)).thenReturn(Collections.emptyList());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reportService.updateImageByEmployeeId(999L, file);
        });

        assertTrue(exception.getMessage().contains("No reports found"));
    }

    @Test
    void testDeleteReport_ReportException() {
        doThrow(new RuntimeException("DB error")).when(reportRepository).deleteById(100L);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reportService.deleteReport(100L);
        });

        assertTrue(exception.getMessage().contains("Error while deleting report"));
    }

}
