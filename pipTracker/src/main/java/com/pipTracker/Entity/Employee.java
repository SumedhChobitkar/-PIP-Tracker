package com.pipTracker.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
@Table(name = "employee")

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,property = "employeeId")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long employeeId;


    private String name;
    private String email;
    @Enumerated(EnumType.STRING)
    private Role role;
    private String department;
    private String designation;
    private String skills;
    private String currentKRA;
    private String kpi;
    private Long managerId;// we need to change after completion of code
    private Long hrId;
    private Long adminId;
    private String photoUrl;
    private LocalDate joiningDate;
    @Enumerated(EnumType.STRING)
    private Status status;


    @Enumerated(EnumType.STRING)
    private RegistrationStatus isRegistered;

    @OneToOne(mappedBy = "employee", cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties("employee")
    private User user;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<FeedBack> feedback = new ArrayList<>();

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Pip> pip = new ArrayList<>();

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("employee")
    private List<SkillGapAnalysis> skillGaps = new ArrayList<>();

    //One Employee Can Have Multiple Reviews
    @OneToMany(mappedBy = "employee",cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonIgnore
    private List<PerformanceReview> performanceReviews;

    //One Employee Can Be reviewer for multiple reviews
    @OneToMany(mappedBy = "reviewer",cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonIgnore
    private List<PerformanceReview> reviewsGiven;

    public Employee(long l, String johnDoe) {
    }
}