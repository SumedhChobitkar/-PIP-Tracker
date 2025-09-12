package com.pipTracker.Repository;

import com.pipTracker.Entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog,Long>
{
    List<AuditLog> findByEntityId(Long entityId);
}
