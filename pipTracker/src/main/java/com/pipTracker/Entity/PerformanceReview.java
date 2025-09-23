package com.pipTracker.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "performance_reviews")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class PerformanceReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    // === Employee being reviewed ===
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    // === Reviewer (Manager/HR) ===
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private Employee reviewer;

    private String reviewPeriod;
    private LocalDate reviewDate;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String scores; // JSON string

    private Double overallRating;

    @Column(length = 1000)
    private String comments;

    @Enumerated(EnumType.STRING)
    private ReviewType reviewType; // MONTHLY / QUARTERLY

    // === File Upload ===
    private String fileName;
    private String fileType;

    @Lob
    @Column(name = "file_data", columnDefinition = "LONGBLOB")
    private byte[] fileData;
}
