package com.pipTracker.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
@Data

@NoArgsConstructor
@AllArgsConstructor

public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;
    private Long createdBy;
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
