package com.pipTracker.Controller;

import com.pipTracker.Controller.AuditLogController;
import com.pipTracker.Entity.AuditLog;
import com.pipTracker.Exception.AuditLogNotFoundException;
import com.pipTracker.Service.AuditLogService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TestAuditLogController {

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private AuditLogController auditLogController;

    @Test
    void testGetAllLogs_Success() {
        AuditLog log = new AuditLog();
        List<AuditLog> logs = List.of(log);

        when(auditLogService.getAllLogs()).thenReturn(logs);

        ResponseEntity<?> response = auditLogController.getAllLogs();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(logs, response.getBody());
        verify(auditLogService, times(1)).getAllLogs();
    }

    @Test
    void testGetAllLogs_EmptyList() {
        when(auditLogService.getAllLogs()).thenReturn(Collections.emptyList());

        ResponseEntity<?> response = auditLogController.getAllLogs();

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals("AuditLog Not Available", response.getBody());
        verify(auditLogService, times(1)).getAllLogs();
    }

    @Test
    void testGetAllLogs_Exception() {
        when(auditLogService.getAllLogs()).thenThrow(new AuditLogNotFoundException("Logs not found"));

        ResponseEntity<?> response = auditLogController.getAllLogs();

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(auditLogService, times(1)).getAllLogs();
    }

    @Test
    void testGetFeedBackLogsById_Success() {
        AuditLog log = new AuditLog();
        List<AuditLog> logs = List.of(log);

        when(auditLogService.getFeedBackLogsById(1L)).thenReturn(logs);

        ResponseEntity<?> response = auditLogController.getFeedBackLogsById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(logs, response.getBody());
        verify(auditLogService, times(1)).getFeedBackLogsById(1L);
    }

    @Test
    void testGetFeedBackLogsById_EmptyList() {
        when(auditLogService.getFeedBackLogsById(1L)).thenReturn(Collections.emptyList());

        ResponseEntity<?> response = auditLogController.getFeedBackLogsById(1L);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals("AuditLog Not Available", response.getBody());
        verify(auditLogService, times(1)).getFeedBackLogsById(1L);
    }

    @Test
    void testGetFeedBackLogsById_Exception() {
        when(auditLogService.getFeedBackLogsById(1L))
                .thenThrow(new AuditLogNotFoundException("Logs not found"));

        ResponseEntity<?> response = auditLogController.getFeedBackLogsById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(auditLogService, times(1)).getFeedBackLogsById(1L);
    }

    @Test
    void testDeleteAuditLog_Success() {
        doNothing().when(auditLogService).deleteAuditLog(1L);

        ResponseEntity<String> response = auditLogController.deleteAuditLog(1L);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals("Deleted Successfully", response.getBody());
        verify(auditLogService, times(1)).deleteAuditLog(1L);
    }

    @Test
    void testDeleteAuditLog_Exception() {
        doThrow(new RuntimeException("Id not Found"))
                .when(auditLogService).deleteAuditLog(1L);

        ResponseEntity<String> response = auditLogController.deleteAuditLog(1L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Id not Found", response.getBody());
        verify(auditLogService, times(1)).deleteAuditLog(1L);
    }
}
