package com.pipTracker.Repository;

import com.pipTracker.Entity.AuditLogArchieve;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogArchieveRepository extends JpaRepository<AuditLogArchieve,Long>
{
    List<AuditLogArchieve> findByLogId(Long logId);
}
