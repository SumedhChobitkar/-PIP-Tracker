package com.pipTracker.Service;

import com.pipTracker.Entity.AuditLogArchieve;

import java.util.List;

public interface AuditLogArchieveService {
    List<AuditLogArchieve> getAllArchieve(Long logId);
    AuditLogArchieve updateArchieve(Long logId);
    void deleteAll();
    void deleteAuditLogArchieve(Long logId);
}
