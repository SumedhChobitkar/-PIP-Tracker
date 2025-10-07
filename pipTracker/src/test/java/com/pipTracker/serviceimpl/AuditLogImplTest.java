package com.pipTracker.serviceimpl;

import com.pipTracker.Entity.Action;
import com.pipTracker.Entity.AuditLog;
import com.pipTracker.Entity.EntityName;
import com.pipTracker.Repository.AuditLogRepository;
import com.pipTracker.ServiceImpl.AuditLogServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuditLogImplTest
{
    @Mock
    AuditLogRepository auditLogRepository;

    @InjectMocks
    AuditLogServiceImpl auditLogService;

    @Test
    void createAuditLogFeedBackTest()
    {
        AuditLog audit=new AuditLog();
        audit.setLogId(1L);
        audit.setRemarks("Nice");
        audit.setAction(Action.CREATE);
        audit.setEntityId(2L);
        audit.setEntityname(EntityName.FEEDBACK);
        audit.setUserId(2L);

        when(auditLogRepository.save(any(AuditLog.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AuditLog log=auditLogService.createAuditLogFeedBack(audit);

        assertEquals(1L,log.getLogId());
        assertEquals("Nice",log.getRemarks());
        assertEquals(Action.CREATE,log.getAction());
        assertEquals(EntityName.FEEDBACK,log.getEntityname());
        assertEquals(2L,log.getUserId());
        assertEquals(2L,log.getEntityId());

        verify(auditLogRepository,times(1)).save(any(AuditLog.class));
    }

    @Test
    void createAuditLogPIPTest()
    {
        AuditLog audit=new AuditLog();
        audit.setLogId(1L);
        audit.setRemarks("Nice");
        audit.setAction(Action.CREATE);
        audit.setEntityId(2L);
        audit.setEntityname(EntityName.PIP);
        audit.setUserId(2L);

        when(auditLogRepository.save(any(AuditLog.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AuditLog log=auditLogService.createAuditLogFeedBack(audit);

        assertEquals(1L,log.getLogId());
        assertEquals("Nice",log.getRemarks());
        assertEquals(Action.CREATE,log.getAction());
        assertEquals(EntityName.PIP,log.getEntityname());
        assertEquals(2L,log.getUserId());
        assertEquals(2L,log.getEntityId());

        verify(auditLogRepository,times(1)).save(any(AuditLog.class));
    }
    @Test
    void createAuditLogSkillGapsTest()
    {
        AuditLog audit=new AuditLog();
        audit.setLogId(1L);
        audit.setRemarks("Nice");
        audit.setAction(Action.CREATE);
        audit.setEntityId(2L);
        audit.setEntityname(EntityName.SKILLGAP);
        audit.setUserId(2L);

        when(auditLogRepository.save(any(AuditLog.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AuditLog log=auditLogService.createAuditLogFeedBack(audit);

        assertEquals(1L,log.getLogId());
        assertEquals("Nice",log.getRemarks());
        assertEquals(Action.CREATE,log.getAction());
        assertEquals(EntityName.SKILLGAP,log.getEntityname());
        assertEquals(2L,log.getUserId());
        assertEquals(2L,log.getEntityId());

        verify(auditLogRepository,times(1)).save(any(AuditLog.class));
    }
    @Test
    void createAuditLogPerformanceReviewTest()
    {
        AuditLog audit=new AuditLog();
        audit.setLogId(1L);
        audit.setRemarks("Nice");
        audit.setAction(Action.CREATE);
        audit.setEntityId(2L);
        audit.setEntityname(EntityName.REVIEW);
        audit.setUserId(2L);

        when(auditLogRepository.save(any(AuditLog.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AuditLog log=auditLogService.createAuditLogFeedBack(audit);

        assertEquals(1L,log.getLogId());
        assertEquals("Nice",log.getRemarks());
        assertEquals(Action.CREATE,log.getAction());
        assertEquals(EntityName.REVIEW,log.getEntityname());
        assertEquals(2L,log.getUserId());
        assertEquals(2L,log.getEntityId());

        verify(auditLogRepository,times(1)).save(any(AuditLog.class));
    }
}
