package com.pipTracker.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table
public class AuditLogArchieve
{

    @Id
    private Long logId;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private EntityName entityname;

    private Long entityId;

    @Enumerated(EnumType.STRING)
    private Action action;

    private LocalDateTime timestamp;

    private String remarks;

    private LocalDateTime deletedAt;

    @Enumerated(EnumType.STRING)
    private ArchiveStatus status;
}
