package com.pipTracker.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long employeeId;

    private String name;
    private String email;
    private String password;
    private String role;
    private String department;
    private String designation;
    private String skills;
    private String currentKRA;
    private String kpi;
    private Long managerId;// we need to change after complitation of code
    private String photoUrl;
    private LocalDate joiningDate;
    private String status;
}
