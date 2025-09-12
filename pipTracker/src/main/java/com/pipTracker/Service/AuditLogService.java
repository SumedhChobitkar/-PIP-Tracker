package com.pipTracker.Service;

import com.pipTracker.Entity.AuditLog;

import java.util.List;

public interface AuditLogService {
    AuditLog createAuditLogFeedBack(AuditLog log);
    AuditLog createAuditLogSkillGap(AuditLog log);
    AuditLog createAuditLogPip(AuditLog log);
    AuditLog createAuditLogPerformanceReview(AuditLog log);
    List<AuditLog> getAllLogs();
    List<AuditLog> getFeedBackLogsById(Long entityId);
    AuditLog updateAuditlogFeedBack(AuditLog newlog);
    AuditLog updateAuditlogSkillGap(AuditLog newlog);
    AuditLog updateAuditlogPip(AuditLog newlog);
    AuditLog updateAuditlogPerformanceReview(AuditLog newlog);
    void deleteAuditLog(Long logId);
}
