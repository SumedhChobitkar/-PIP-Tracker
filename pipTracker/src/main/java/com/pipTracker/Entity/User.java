package com.pipTracker.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String name;
    private String email;
    private String password;
    private boolean recentlyChangedPassword = false;
    private LocalDateTime lastPasswordChangedAt;
    @Enumerated(EnumType.STRING)
    private Role role;
    private String department;
    private String designation;
    private String skills;
    private String currentKRA;
    private String kpi;
    private Long managerId;
    private String photoUrl;
    private LocalDate joiningDate;
    private String status;
    private String Isregistered;

    private LocalDateTime lastLoginTime;


    @OneToOne
    @JoinColumn(name = "employee_id")
    @JsonIgnoreProperties("user")
    private Employee employee;
}
