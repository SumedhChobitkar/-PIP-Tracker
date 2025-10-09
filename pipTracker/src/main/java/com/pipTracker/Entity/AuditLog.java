package com.pipTracker.Entity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table
public class AuditLog
{
    @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "entityname",length = 50)
    private EntityName entityname;

    private Long entityId;
    @Enumerated(EnumType.STRING)
    private Action action; // e.g., CREATE, UPDATE, DELETE
    private LocalDateTime timestamp;
    private String remarks;
}
