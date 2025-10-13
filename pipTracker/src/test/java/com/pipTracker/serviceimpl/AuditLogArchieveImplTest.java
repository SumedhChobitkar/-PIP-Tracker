package com.pipTracker.serviceimpl;

import com.pipTracker.Entity.*;
import com.pipTracker.Exception.AuditLogArchieveNotFoundException;
import com.pipTracker.Repository.AuditLogArchieveRepository;
import com.pipTracker.Repository.AuditLogRepository;
import com.pipTracker.ServiceImpl.AuditLogArchieveServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.slf4j.Logger;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuditLogArchieveImplTest {

    @InjectMocks
    private AuditLogArchieveServiceImpl auditLogArchieveService;

    @Mock
    private AuditLogArchieveRepository auditLogArchieveRepository;

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private Logger logger;

    private AuditLogArchieve archieve;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        archieve = new AuditLogArchieve();
        archieve.setLogId(1L);
        archieve.setEntityname(EntityName.FEEDBACK);
        archieve.setEntityId(10L);
        archieve.setAction(Action.CREATE);
        archieve.setRemarks("Testing");
        archieve.setStatus(ArchiveStatus.DELETED);
    }
    @Test
    void testGetAllArchieve() {
        when(auditLogArchieveRepository.findByLogId(1L))
                .thenReturn(Collections.singletonList(archieve));

        List<AuditLogArchieve> result = auditLogArchieveService.getAllArchieve(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(auditLogArchieveRepository, times(1)).findByLogId(1L);
    }
    @Test
    void testGetAllArchieve_NotFound() {
        when(auditLogArchieveRepository.findByLogId(2L)).thenReturn(Collections.emptyList());

        assertThrows(AuditLogArchieveNotFoundException.class,
                () -> auditLogArchieveService.getAllArchieve(2L));
    }

    @Test
    void testUpdateArchieve() {
        when(auditLogArchieveRepository.findById(1L))
                .thenReturn(Optional.of(archieve));
        when(auditLogRepository.save(any(AuditLog.class)))
                .thenReturn(new AuditLog());
        when(auditLogArchieveRepository.save(any(AuditLogArchieve.class)))
                .thenReturn(archieve);

        AuditLogArchieve updated = auditLogArchieveService.updateArchieve(1L);

        assertNotNull(updated);
        assertEquals(ArchiveStatus.RESTORED, updated.getStatus());
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
        verify(auditLogArchieveRepository, times(1)).save(any(AuditLogArchieve.class));
    }

    @Test
    void testUpdateArchieve_NotFound() {
        when(auditLogArchieveRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                auditLogArchieveService.updateArchieve(99L));
    }
    @Test
    void testUpdateArchieve_StatusNotDeleted() {
        archieve.setStatus(ArchiveStatus.RESTORED);
        when(auditLogArchieveRepository.findById(1L))
                .thenReturn(Optional.of(archieve));

        assertThrows(AuditLogArchieveNotFoundException.class,
                () -> auditLogArchieveService.updateArchieve(1L));
    }

    @Test
    void testDeleteAll_Positive() {
        assertDoesNotThrow(() -> auditLogArchieveService.deleteAll());
        verify(auditLogArchieveRepository, times(1)).deleteAll();
    }

    @Test
    void testDeleteAll() {
        doThrow(new RuntimeException("Delete failed"))
                .when(auditLogArchieveRepository).deleteAll();

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> auditLogArchieveService.deleteAll());
        assertEquals("Delete failed", ex.getMessage());
    }

    @Test
    void testDeleteAuditLogArchieve() {
        when(auditLogArchieveRepository.findById(1L))
                .thenReturn(Optional.of(archieve));

        auditLogArchieveService.deleteAuditLogArchieve(1L);

        verify(auditLogArchieveRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteAuditLogArchieve_NotFound() {
        when(auditLogArchieveRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(AuditLogArchieveNotFoundException.class,
                () -> auditLogArchieveService.deleteAuditLogArchieve(1L));
    }
}

