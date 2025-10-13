package com.pipTracker.controller;


import com.pipTracker.Controller.AuditLogArchieveController;
import com.pipTracker.Entity.AuditLogArchieve;
import com.pipTracker.Exception.AuditLogArchieveNotFoundException;
import com.pipTracker.Service.AuditLogArchieveService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TestAuditLogArchieveController {

    @Mock
    private AuditLogArchieveService auditLogArchieveService;

    @InjectMocks
    private AuditLogArchieveController auditLogArchieveController;

    @Test
    void testGetAllArchieve_Success() {
        AuditLogArchieve archieve = new AuditLogArchieve();
        when(auditLogArchieveService.getAllArchieve(1L)).thenReturn(List.of(archieve));

        ResponseEntity<?> response = auditLogArchieveController.getAllArchieve(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(archieve), response.getBody());
        verify(auditLogArchieveService, times(1)).getAllArchieve(1L);
    }

    @Test
    void testGetAllArchieve_Exception() {
        when(auditLogArchieveService.getAllArchieve(1L))
                .thenThrow(new RuntimeException("ID not found"));

        ResponseEntity<?> response = auditLogArchieveController.getAllArchieve(1L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("ID not found", response.getBody());
        verify(auditLogArchieveService, times(1)).getAllArchieve(1L);
    }

    @Test
    void testUpdateArchieve_Success() {
        AuditLogArchieve archieve = new AuditLogArchieve();
        when(auditLogArchieveService.updateArchieve(1L)).thenReturn(archieve);

        ResponseEntity<?> response = auditLogArchieveController.updateArchieve(1L);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals("Data of logId 1 restored in AuditLog!", response.getBody());
        verify(auditLogArchieveService, times(1)).updateArchieve(1L);
    }

    @Test
    void testUpdateArchieve_NotFoundException() {
        when(auditLogArchieveService.updateArchieve(1L))
                .thenThrow(new AuditLogArchieveNotFoundException("Already restored"));

        ResponseEntity<?> response = auditLogArchieveController.updateArchieve(1L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("ID already restored in Audit Log", response.getBody());
        verify(auditLogArchieveService, times(1)).updateArchieve(1L);
    }

    @Test
    void testUpdateArchieve_IllegalArgumentException() {
        when(auditLogArchieveService.updateArchieve(1L))
                .thenThrow(new IllegalArgumentException("Invalid ID"));

        ResponseEntity<?> response = auditLogArchieveController.updateArchieve(1L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Validations Issues"));
        verify(auditLogArchieveService, times(1)).updateArchieve(1L);
    }

    @Test
    void testUpdateArchieve_GenericException() {
        when(auditLogArchieveService.updateArchieve(1L))
                .thenThrow(new RuntimeException("Not found"));

        ResponseEntity<?> response = auditLogArchieveController.updateArchieve(1L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Id not found in AuditLog", response.getBody());
        verify(auditLogArchieveService, times(1)).updateArchieve(1L);
    }

    @Test
    void testDeleteAll_Success() {
        doNothing().when(auditLogArchieveService).deleteAll();

        ResponseEntity<?> response = auditLogArchieveController.deleteAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("DELETED SUCCESSFULLY!!!", response.getBody());
        verify(auditLogArchieveService, times(1)).deleteAll();
    }

    @Test
    void testDeleteAll_Exception() {
        doThrow(new RuntimeException("Error while deleting"))
                .when(auditLogArchieveService).deleteAll();

        ResponseEntity<?> response = auditLogArchieveController.deleteAll();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error while deleting!!!", response.getBody());
        verify(auditLogArchieveService, times(1)).deleteAll();
    }

    @Test
    void testDeleteAuditLogArchieve_Success() {
        doNothing().when(auditLogArchieveService).deleteAuditLogArchieve(1L);

        ResponseEntity<?> response = auditLogArchieveController.deleteAuditLogArchieve(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("DELETED SUCCESSFULLY!!!", response.getBody());
        verify(auditLogArchieveService, times(1)).deleteAuditLogArchieve(1L);
    }

    @Test
    void testDeleteAuditLogArchieve_Exception() {
        doThrow(new RuntimeException("ID Not Found!!!!"))
                .when(auditLogArchieveService).deleteAuditLogArchieve(1L);

        ResponseEntity<?> response = auditLogArchieveController.deleteAuditLogArchieve(1L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("ID Not Found!!!!", response.getBody());
        verify(auditLogArchieveService, times(1)).deleteAuditLogArchieve(1L);
    }
}
