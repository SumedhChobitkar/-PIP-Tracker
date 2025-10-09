/*package com.pipTracker.Entity;

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
    private String fileName;

    private Long fileSize;

    @Transient // not stored in DB
    private String imageUrl; // generated dynamically in controller/service
}
*/
/*package com.pipTracker.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    private Long createdBy;

    @Enumerated(EnumType.STRING)
    private ReportType reportType;

    private LocalDateTime generatedOn;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    // Image/File fields
    private String fileName;
    @Lob
    @JsonIgnore
    private byte[] fileData;

    private String fileType;
    private Long fileSize;

    // For easier access from frontend or Swagger
    private String photoUrl;
}
*/
/*package com.pipTracker.Entity;

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
    private String fileName;

    private Long fileSize;

    @Transient // not stored in DB
    private String imageUrl; // generated dynamically in controller/service
}
*/
package com.pipTracker.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    public Report(long l, long l1, ReportType reportType, LocalDateTime now, Object o, Object o1, Object o2) {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    private Long createdBy;

    @Enumerated(EnumType.STRING)
    private ReportType reportType;

    private LocalDateTime generatedOn;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    // Image/File fields
    private String fileName;
    @Lob
    @JsonIgnore
    private byte[] fileData;

    private String fileType;
    private Long fileSize;

    // For easier access from frontend or Swagger
    private String photoUrl;


}


