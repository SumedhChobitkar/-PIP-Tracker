package com.pipTracker.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
@Data
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;
    private String createdBy;
    @Enumerated(EnumType.STRING)
    private ReportType reportType; // e.g., PERFORMANCE, PIP, FEEDBACK
    private LocalDateTime generatedOn;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;
    @Lob
    private byte[] file;
    private  String fileType;
}
