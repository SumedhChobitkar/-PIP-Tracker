/*package com.pipTracker.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "performance_reviews")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PerformanceReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    @JsonIgnoreProperties({"user", "pip", "feedback", "skillGaps", "hibernateLazyInitializer", "handler"})
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reviewer_id", nullable = false)
    @JsonIgnoreProperties({"user", "pip", "feedback", "skillGaps", "hibernateLazyInitializer", "handler"})
    private Employee reviewer;

    private String reviewPeriod;
    private LocalDate reviewDate;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String scores;

    private Double overallRating;

    @Column(length = 1000)
    private String comments;

    @Enumerated(EnumType.STRING)
    private ReviewType reviewType;

    private String fileName;
    private String fileType;

    @Lob
    @Column(name = "file_data", columnDefinition = "LONGBLOB")
    private byte[] fileData;

    @PrePersist
    public void prePersist() {
        if (reviewDate == null) reviewDate = LocalDate.now();
    }
}
*/
package com.pipTracker.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PerformanceReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    //Employee being reviewed
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    //Reviewer (Manager/HR)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private Employee reviewer;

    private String reviewPeriod;
    private LocalDate reviewDate;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String scores; // JSON string for KRA/KPI

    private Double overallRating;

    @Column(length = 1000)
    private String comments;

    @Enumerated(EnumType.STRING)
    private ReviewType reviewType; // MONTHLY / QUARTERLY

    //File Upload Fields
    private String fileName;
    private String fileType;

    @Lob
    @JsonIgnore
    @Column(name = "file_data", columnDefinition = "LONGBLOB")
    private byte[] fileData;

    //Transient field for download URL
    @Transient
    private String fileUrl;

    public String getFileUrl() {
        if (this.reviewId != null) {
            return "/api/performance-reviews/" + this.reviewId + "/download";
        }
        return null;
    }
}
